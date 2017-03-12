package com.github.staticdebug.spoon;

import com.github.staticdebug.runner.DynamicTestRunner;
import spoon.reflect.declaration.CtType;

public class CtTest {
	private CtType<?> type;
	private DynamicTestRunner.TEST_TYPE testType;

	public CtTest(CtType<?> type, DynamicTestRunner.TEST_TYPE testType) {
		this.type = type;
		this.testType = testType;
	}

	public CtType<?> getAst() {
		return type;
	}

	public DynamicTestRunner.TEST_TYPE getType() {
		return testType;
	}

	public void run() {
		new DynamicTestRunner(null).run(this);
	}
}
