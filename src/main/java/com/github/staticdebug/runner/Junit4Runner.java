package com.github.staticdebug.runner;

import com.github.staticdebug.spoon.CtTest;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.List;

public class Junit4Runner {

	private CtTest test;
	private TestSuiteNotifier notifier;

	public Junit4Runner(CtTest test, TestSuiteNotifier notifier) {
		this.test = test;
		this.notifier = notifier;
	}

	public List<Description> run() {
		try {
			Class<?> aClass = Thread.currentThread().getContextClassLoader().loadClass(test.getAst().getQualifiedName());

			BlockJUnit4ClassRunner runner = new BlockJUnit4ClassRunner(aClass);

			RunNotifier notifier = new RunNotifier();
			notifier.addListener(this.notifier);

			runner.run(notifier);

			return this.notifier.getDescriptions();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
