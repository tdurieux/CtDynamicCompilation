package com.github.staticdebug.runner;

import com.github.staticdebug.spoon.CtTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;
import spoon.Launcher;

import java.util.List;

public class DynamicTestRunnerTest {

	@Test
	public void testFindTestClasses() throws Exception {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/examples");

		launcher.getModelBuilder().setSourceClasspath(System.getProperty("java.class.path").split(":"));
		launcher.buildModel();

		DynamicTestRunner runner = new DynamicTestRunner(launcher);

		List<CtTest> testClasses = runner.findTestClasses();
		Assert.assertEquals(1, testClasses.size());
	}

	@Test
	public void testRunAllTests() throws Exception {
		Launcher launcher = new Launcher();
		launcher.addInputResource("./src/test/resources/examples");

		launcher.getModelBuilder().setSourceClasspath(System.getProperty("java.class.path").split(":"));
		launcher.buildModel();

		DynamicTestRunner runner = new DynamicTestRunner(launcher);
		List<Description> testResults = runner.runAllTests();
		Assert.assertEquals(9, testResults.size());
	}

}