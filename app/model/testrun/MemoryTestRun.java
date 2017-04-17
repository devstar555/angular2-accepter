package model.testrun;

import java.util.Date;
import java.util.List;

class MemoryTestRun extends TestRun {

	private Long id;

	MemoryTestRun() {
	}

	MemoryTestRun(long id, Date start, Date end, List<TestCase> testCases) {
		this.id = id;
		this.setStart(start);
		this.setEnd(end);
		this.setTestCases(testCases);
		this.setTestRunResult(testCases);
	}

	@Override
	public Long getId() {
		return id;
	}

}
