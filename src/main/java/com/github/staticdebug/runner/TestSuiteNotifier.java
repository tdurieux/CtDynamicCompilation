package com.github.staticdebug.runner;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.List;

class TestSuiteNotifier extends RunListener {
	private List<Description> descriptions = new ArrayList<>();
	private List<RunListener> listeners = new ArrayList<>();

	@Override
	public void testRunStarted(Description description) throws Exception {
		super.testRunStarted(description);
		for (RunListener listener : listeners) {
			listener.testRunStarted(description);
		}
	}

	@Override
	public void testRunFinished(Result result) throws Exception {
		super.testRunFinished(result);
		for (RunListener listener : listeners) {
			listener.testRunFinished(result);
		}
	}

	@Override
	public void testStarted(Description description) throws Exception {
		super.testStarted(description);
		for (RunListener listener : listeners) {
			listener.testStarted(description);
		}
	}

	@Override
	public void testFailure(Failure failure) throws Exception {
		super.testFailure(failure);
		for (RunListener listener : listeners) {
			listener.testFailure(failure);
		}
	}

	@Override
	public void testAssumptionFailure(Failure failure) {
		super.testAssumptionFailure(failure);
		for (RunListener listener : listeners) {
			listener.testAssumptionFailure(failure);
		}
	}

	@Override
	public void testIgnored(Description description) throws Exception {
		super.testIgnored(description);
		for (RunListener listener : listeners) {
			listener.testIgnored(description);
		}
	}

	@Override
	public void testFinished(Description description) throws Exception {
		super.testFinished(description);
		for (RunListener listener : listeners) {
			listener.testFinished(description);
		}
		descriptions.add(description);
	}

	public List<Description> getDescriptions() {
		return descriptions;
	}

	public void addListener(RunListener runListener) {
		this.listeners.add(runListener);
	}

	public void removeListener(RunListener runListener) {
		this.listeners.remove(runListener);
	}
}