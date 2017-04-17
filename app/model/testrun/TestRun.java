package model.testrun;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class TestRun implements Comparable<TestRun> {

	private Date start;
	private Date end;
	protected TestResult result;
	private List<TestCase> testCases;

	TestRun(Date start, Date end, List<TestCase> testCases) {
		this.start = start;
		this.end = end;
		this.testCases = testCases;
	}
	TestRun() {
		this.testCases = new ArrayList<TestCase>();
	}

	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public List<TestCase> getTestCases() {
		return testCases;
	}
	public void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	public TestResult getResult() {
		return result;
	}

	public abstract Long getId();

	protected void setTestRunResult(List<TestCase> list) {
		boolean result = list.stream().anyMatch(test -> test.getResult().equals(TestResult.FAILED));
		this.result = result ? TestResult.FAILED : TestResult.PASSED;
	}

	public int compareTo(TestRun other) {
		return new CompareToBuilder().append(this.getStart(), other.getStart()).toComparison();
	}

}
