package model.testrun;

import java.util.List;

public interface TestRunRepository {
	TestRun saveTestRun(TestRun test);
	List<TestRun> getTestRuns(int limit);
	void cleanTestRunsOlderThan(long seconds);
	TestRun newTestRun();
	TestCase newTestCase();
}
