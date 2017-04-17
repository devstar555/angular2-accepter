package model.testrun;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import db.DbTestCase;
import db.DbTestRun;

class DatabaseTestRun extends TestRun {

	private Long id;

	@Override
	public Long getId() {
		return id;
	}
	DatabaseTestRun() {
		this.setTestCases(new ArrayList<TestCase>());
	}

	DatabaseTestRun(DbTestRun testRun) {
		this.setStart(testRun.getStart());
		this.setEnd(testRun.getEnd());
		this.setTestCases(getTestCasesFromDbTestCases(testRun.getTestCases()));
		this.id = testRun.getId();
		this.setTestRunResult(this.getTestCases());
	}

	private List<TestCase> getTestCasesFromDbTestCases(List<DbTestCase> testCases) {
		TreeMap<Long, TestCase> result = new TreeMap<Long, TestCase>();
		for (DbTestCase current : testCases) {
			TestCase test = new DatabaseTestCase(current);
			result.put(test.getId(), test);
		}

		return new ArrayList<TestCase>(result.values());
	}

}
