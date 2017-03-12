package com.github.staticdebug.compiler;

import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.DefaultJavaPrettyPrinter;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class DynamicCompiler {

	private List<String> options;
	private final JavaCompiler compiler;
	private DiagnosticCollector<JavaFileObject> diagnostics;
	private final VirtualFileObjectManager fileManager;
	private Launcher launcher;
	private Map<String, VirtualCompiledSourceFileObject> compiledClasses;

	public DynamicCompiler(Launcher launcher) {
		this.launcher = launcher;
		this.compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager standardFileManager = compiler.getStandardFileManager(diagnostics, null, null);
		fileManager = new VirtualFileObjectManager(standardFileManager);
		compiledClasses = new HashMap<>();

		clear();
	}

	public void clear() {
		diagnostics = new DiagnosticCollector<JavaFileObject>();
		this.options = createOptions();
	}

	public ClassLoader compile() {
		return compile(launcher.getModel());
	}

	public ClassLoader compile(CtModel scope) {
		Map<String, JavaFileObject> units = createUnits(scope);

		List<JavaFileObject> sources = new ArrayList<>();
		for (JavaFileObject sourceFile : units.values()) {
			if (sourceFile instanceof VirtualSourceFileObject) {
				sources.add(sourceFile);
			}
		}

		if (!sources.isEmpty()) {
			fileManager.setSourceFiles(units);

			JavaCompiler.CompilationTask task = compiler.getTask(
					null,
					fileManager,
					diagnostics,
					options, null,
					sources);
			runCompilationTask(task);

			this.compiledClasses = fileManager.getClassFiles();
		}
		return new BytecodeClassLoader(getURLforClasspath(), compiledClasses);
	}

	private URL[] getURLforClasspath() {
		String[] sourceClasspath = launcher.getModelBuilder().getSourceClasspath();
		if (sourceClasspath == null) {
			sourceClasspath = new String[0];
		}
		URL[] urls = new URL[sourceClasspath.length];
		for (int i = 0; i < sourceClasspath.length; i++) {
			String path = sourceClasspath[i];
			try {
				urls[i] = new URL("file", "", path);
			} catch (MalformedURLException ignore) {
			}
		}
		return urls;
	}

	private Map<String, JavaFileObject> createUnits(CtModel scope) {
		Map<String, JavaFileObject> output = new HashMap<>();

		Collection<CtType<?>> allTypes = scope.getAllTypes();
		for (CtType<?> ctType : allTypes) {
			if (ctType.isTopLevel()) {
				output.put(ctType.getQualifiedName(), createUnit(ctType));
			}
		}
		return output;
	}

	private JavaFileObject createUnit(CtType element) {
		DefaultJavaPrettyPrinter printer = new DefaultJavaPrettyPrinter(launcher.getEnvironment());

		CompilationUnit cu = null;
		if (element.getPosition() != null) {
			cu = element.getPosition().getCompilationUnit();
			if (cu == null) {
				cu = element.getFactory().CompilationUnit().create(element.getQualifiedName());
				cu.setDeclaredPackage(element.getPackage());
			}
		}

		List<CtType<?>> toBePrinted = new ArrayList<>();
		toBePrinted.add(element);

		printer.calculate(cu, toBePrinted);
		String result = printer.getResult();

		for (VirtualCompiledSourceFileObject compiledClass : compiledClasses.values()) {
			if (compiledClass.getSourceContent().equals(result)) {
				return compiledClass;
			}
		}
		return new VirtualSourceFileObject(element.getSimpleName(), result);
	}

	private void runCompilationTask(JavaCompiler.CompilationTask task) {
		if (!task.call()) {
			Collection<String> errors = asList("[Compilation errors]");
			for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
				errors.add(diagnostic.toString());
			}
			throw new RuntimeException("Aborting: dynamic compilation failed");
		}
	}

	/**
	 * Create the options list for the compilation
	 * @return the compilation options
	 */
	private List<String> createOptions() {
		// it is not possible to compile a newest version of java
		int complianceLevel = Math.min(getJavaVersion(), launcher.getFactory().getEnvironment().getComplianceLevel());

		List<String> options = new ArrayList<>(asList("-nowarn", "-source", "1." + complianceLevel, "-target", "1." + complianceLevel));
		String[] sourceClasspath = launcher.getModelBuilder().getSourceClasspath();
		if (sourceClasspath != null && sourceClasspath.length > 0) {
			options.add("-cp");
			StringBuilder sb = new StringBuilder();
			for (String path : sourceClasspath) {
				sb.append(path);
				sb.append(File.pathSeparatorChar);
			}
			options.add(sb.toString());
		}
		return options;
	}

	private int getJavaVersion() {
		String version = System.getProperty("java.version");
		int pos = version.indexOf('.');
		return Integer.parseInt(version.substring(pos + 1, pos + 2));
	}
}
