package model.testrun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.avaje.ebean.QueryIterator;

import db.DbTestCase;
import db.DbTestRun;
import model.config.Config;
import play.db.ebean.Transactional;
import util.Utils;

class DatabaseTestRunRepository implements TestRunRepository {

	DatabaseTestRunRepository() {
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
		// if it passes all previous checks now we create DB elements and save
		List<DbTestCase> dbTestCases = new ArrayList<>();
		for (TestCase curr : set) {
			curr.setName(StringUtils.trimToNull(curr.getName()));
			dbTestCases.add(new DbTestCase(curr.getName(), curr.getExpected(), curr.getActual()));
		}
		DbTestRun run = new DbTestRun(test.getStart(), test.getEnd(), dbTestCases);
		run.save();
		return new DatabaseTestRun(run);
	}

	@Override
	public List<TestRun> getTestRuns(int limit) {
		List<DbTestRun> list = DbTestRun.FINDER.order(DbTestRun.ORDER_BY_START_DATE_DESC).setMaxRows(limit).findList();
		return list.stream().map(testRun -> new DatabaseTestRun(testRun)).collect(Collectors.toList());
	}

	@Override
	public TestRun newTestRun() {
		return new DatabaseTestRun();
	}

	@Override
	public TestCase newTestCase() {
		return new DatabaseTestCase();
	}

	@Override
	@Transactional
	public void cleanTestRunsOlderThan(long seconds) {
		if (seconds < Config.getCleanData())
			throw new IllegalArgumentException(
					seconds + " seconds is below allowed minimum of " + Config.getCleanData());

		QueryIterator<DbTestRun> it = null;

		try {
			it = DbTestRun.FINDER.where().lt(DbTestRun.COLUMN_END, Utils.getDateBefore(seconds)).findIterate();

			while (it.hasNext()) {
				DbTestRun old = it.next();
				DbTestCase.FINDER.where().eq(DbTestCase.COLUMN_TEST_RUN_ID, old.getId()).delete();
				old.delete();
			}
		} finally {
			if (it != null)
				it.close();
		}

	}

}
