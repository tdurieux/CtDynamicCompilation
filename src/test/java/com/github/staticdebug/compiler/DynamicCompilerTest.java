package com.github.staticdebug.compiler;

import org.junit.Test;
import spoon.Launcher;
import spoon.reflect.factory.Factory;

public class DynamicCompilerTest {

	@Test
	public void testCompileNewClass() throws ClassNotFoundException {
		Launcher launcher = new Launcher();

		final String className = "MyClass";

		Factory factory = launcher.getFactory();
		factory.Class().create(className);

		final DynamicCompiler compiler = new DynamicCompiler(launcher);
		final ClassLoader classLoader = compiler.compile();

		new Thread(new Runnable() {
			@Override
			public void run() {
				Thread.currentThread().setContextClassLoader(classLoader);
				try {
					Class<?> aClass = classLoader.loadClass(className);
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			}
		}).run();
	}

	@Test
	public void testCompile() throws ClassNotFoundException {
		final Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/examples");

		launcher.getModelBuilder().setSourceClasspath(System.getProperty("java.class.path").split(":"));
		launcher.buildModel();


		final Factory factory = launcher.getFactory();

		final DynamicCompiler compiler = new DynamicCompiler(launcher);
		final ClassLoader classLoader = compiler.compile();

		classLoader.loadClass("Example");
	}
}