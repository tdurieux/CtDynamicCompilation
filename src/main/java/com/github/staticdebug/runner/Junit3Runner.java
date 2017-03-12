package com.github.staticdebug.runner;

import com.github.staticdebug.spoon.CtTest;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;

import java.util.List;

public class Junit3Runner {
	private CtTest test;
	private TestSuiteNotifier notifier;

	public Junit3Runner(CtTest test, TestSuiteNotifier notifier) {
		this.test = test;
		this.notifier = notifier;
	}

	public List<Description> run() {
		try {
			Class<?> aClass = Class.forName(test.getAst().getQualifiedName());

			JUnitCore junit = new JUnitCore();
			junit.addListener(notifier);
			junit.run(aClass);
			return null;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}


	}
}
