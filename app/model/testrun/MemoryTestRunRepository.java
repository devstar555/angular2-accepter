package model.testrun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang3.StringUtils;

import model.config.Config;
import util.Utils;

class MemoryTestRunRepository implements TestRunRepository {

	private final AtomicLong testRunId = new AtomicLong(1);
	private final AtomicLong testCaseId = new AtomicLong(1);
	private final List<TestRun> testRuns = new ArrayList<>();

	public MemoryTestRunRepository() {
	}

	@Override
	public TestRun saveTestRun(TestRun test) {

		if (test.getStart() == null)
			throw new IllegalArgumentException("startDate is required");
		if (test.getEnd() == null)
			throw new IllegalArgumentException("endDate is required");
		if (test.getTestCases() == null)
			throw new IllegalArgumentException("at least one testcase is required");
		if (test.getTestCases().size() == 0)
			throw new IllegalArgumentException("at least one testcase is required");

		HashSet<TestCase> set = new HashSet<>();
		HashMap<String, TestCase> map = new HashMap<>();
		for (TestCase curr : test.getTestCases()) {
			if (curr.hasEmptyValues()) {
				throw new IllegalArgumentException("a testcase cannot have empty fields");
			}

			if (set.add(curr) == false) {
				TestCase old = map.get(curr.getName());
				if (old.isIdentic(curr)) {
					map.remove(curr.getName());
				} else if (old.hasConflicts(curr)) {
					throw new IllegalArgumentException("conflict on testcase " + curr.getName()
							+ " with duplicate name and different expected or actual values");
				}
			}
			map.put(curr.getName(), curr);
		}
		// if it passes all previous checks we store it in memory

		// we add id for the testcases
		List<TestCase> testCases = new ArrayList<TestCase>();

		for (TestCase curr : test.getTestCases()) {
			curr.setName(StringUtils.trimToNull(curr.getName()));
			testCases.add(new MemoryTestCase(testCaseId.getAndIncrement(), curr));

		}

		final TestRun result = new MemoryTestRun(testRunId.getAndIncrement(), test.getStart(), test.getEnd(),
				testCases);
		testRuns.add(result);
		return result;
	}

	@Override
	public List<TestRun> getTestRuns(int limit) {
		List<TestRun> resultList = new ArrayList<TestRun>();
		resultList = testRuns.subList(0, Math.min(testRuns.size(), limit));
		Collections.sort(resultList);

		return resultList;
	}

	@Override
	public TestRun newTestRun() {
		return new MemoryTestRun();
	}

	@Override
	public TestCase newTestCase() {
		return new MemoryTestCase();
	}

	@Override
	public synchronized void cleanTestRunsOlderThan(long seconds) {
		if (seconds < Config.getCleanData())
			throw new IllegalArgumentException(
					seconds + " seconds is below allowed minimum of " + Config.getCleanData());

		testRuns.removeIf(test -> test.getEnd().before(Utils.getDateBefore(seconds)));
	}

}
