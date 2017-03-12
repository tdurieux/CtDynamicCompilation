package com.github.staticdebug.runner;

import com.github.staticdebug.compiler.DynamicCompiler;
import com.github.staticdebug.spoon.CtTest;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.factory.Factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DynamicTestRunner {

	public enum TEST_TYPE {
		NONE, // it is not a test class
		JUNIT3,
		JUNIT4,
		JUNIT5
	}

	private final DynamicCompiler compiler;
	private final Launcher launcher;
	private final List<RunListener> listeners = new ArrayList<>();

	public DynamicTestRunner(Launcher launcher) {
		this.launcher = launcher;
		this.compiler = new DynamicCompiler(launcher);
	}

	public List<Description> runAllTests() {
		return runAllTests(launcher.getModel());
	}

	public List<Description> runAllTests(CtModel scope) {
		List<Description> output = new ArrayList<>();
		for (CtTest test : findTestClasses(scope)) {
			List<Description> description = run(test, compiler);
			output.addAll(description);
		}
		return output;
	}

	public List<Description> run(final CtTest test) {
		return run(test, new DynamicCompiler(launcher));
	}

	public List<Description> run(final CtTest test, final DynamicCompiler compiler) {
		return run(test, compiler.compile());
	}

	public List<Description> run(final CtTest test, final ClassLoader classLoader) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<List<Description>> submit = executor.submit(new Callable<List<Description>>() {
			@Override
			public List<Description> call() throws Exception {
				Thread.currentThread().setContextClassLoader(classLoader);

				TestSuiteNotifier notifier = new TestSuiteNotifier();
				for (RunListener runListener : listeners) {
					notifier.addListener(runListener);
				}

				if (test.getType() == TEST_TYPE.JUNIT3) {
					new Junit3Runner(test, notifier).run();
				} else if (test.getType() == TEST_TYPE.JUNIT4
						|| test.getType() == TEST_TYPE.JUNIT5) {
					new Junit4Runner(test, notifier).run();
				}

				return notifier.getDescriptions();
			}
		});
		try {
			executor.shutdown();
			return submit.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public void run(CtClass test) {
		TEST_TYPE testType = isTestClass(test);
		if (testType == TEST_TYPE.NONE) {
			throw new RuntimeException(test + " is not a test");
		}
		run(new CtTest(test, testType));
	}

	public List<CtTest> findTestClasses() {
		return findTestClasses(launcher.getModel());
	}

	public List<CtTest> findTestClasses(CtModel scope) {
		List<CtTest> output = new ArrayList<>();

		Collection<CtType<?>> types = scope.getAllTypes();
		for (CtType<?> type : types) {
			TEST_TYPE testType = isTestClass(type);
			if (testType != TEST_TYPE.NONE) {
				output.add(new CtTest(type, testType));
			}
		}
		return output;
	}

	private TEST_TYPE isTestClass(CtType<?> type) {
		if (type.getModifiers().contains(ModifierKind.ABSTRACT)) {
			return TEST_TYPE.NONE;
		}

		if (isJunit3(type))
			return TEST_TYPE.JUNIT3;

		if (isJunit4(type))
			return TEST_TYPE.JUNIT4;

		if (isJunit5(type))
			return TEST_TYPE.JUNIT5;

		return TEST_TYPE.NONE;
	}

	/**
	 * Detect if the type is a junit 5 test class
	 * @param type
	 * @return true is type is a test
	 */
	private boolean isJunit5(CtType<?> type) {
		// TODO junit 5
		return false;
	}

	/**
	 * Detect if the type is a junit 4 test class
	 * @param type
	 * @return true is type is a test
	 */
	private boolean isJunit4(CtType<?> type) {
		return !type.getAnnotatedChildren(Test.class).isEmpty();
	}

	/**
	 * Detect if the type is a junit 3 test class
	 * @param type
	 * @return true is type is a test
	 */
	private boolean isJunit3(CtType<?> type) {
		Factory factory = type.getFactory();
		return type.isSubtypeOf(factory.createCtTypeReference(TestCase.class));
	}

	public void addListener(RunListener listener) {
		listeners.add(listener);
	}

	public void removeListener(RunListener listener) {
		listeners.remove(listener);
	}
}
