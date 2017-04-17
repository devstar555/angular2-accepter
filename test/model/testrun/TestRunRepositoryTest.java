package model.testrun;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.config.Config;
import model.config.ConfigRepositoryFactory;

public abstract class TestRunRepositoryTest {

	protected abstract TestRunRepository newRepository();

	private TestRunRepository repository;

	private static void resetModelFactories() {
		ConfigRepositoryFactory.FACTORY = ConfigRepositoryFactory
				.factoryCaching(ConfigRepositoryFactory.factoryMemory());
	}

	@Before
	public void beforeRepositoryTest() {
		resetModelFactories();
		this.repository = newRepository();
	}

	@After
	public void afterRepositoryTest() {
		this.repository = null;
		resetModelFactories();
	}

	@Test
	public void cleanTestRuns() {
		Config.setCleanData(0L);

		final TestCase tc1 = repository.newTestCase();
		tc1.setName("testCase1");
		tc1.setActual("hello");
		tc1.setExpected("hello");

		final TestCase tc2 = repository.newTestCase();
		tc2.setName("testCase2");
		tc2.setActual("999");
		tc2.setExpected("999");

		final List<TestCase> testCases = new ArrayList<>();
		testCases.add(tc1);
		testCases.add(tc2);

		assertEquals(0, repository.getTestRuns(20).size());

		final TestRun testRun1Day = repository.newTestRun();
		testRun1Day.setStart(new Date(new Date().getTime() - 1000L * 3600 * 24 * 1));
		testRun1Day.setEnd(new Date(testRun1Day.getStart().getTime() + 1000L * 3600));
		testRun1Day.setTestCases(testCases);
		repository.saveTestRun(testRun1Day);

		final TestRun testRun3Day = repository.newTestRun();
		testRun3Day.setStart(new Date(new Date().getTime() - 1000L * 3600 * 24 * 3));
		testRun3Day.setEnd(new Date(testRun3Day.getStart().getTime() + 1000L * 3600));
		testRun3Day.setTestCases(testCases);
		repository.saveTestRun(testRun3Day);

		final TestRun testRun5Day = repository.newTestRun();
		testRun5Day.setStart(new Date(new Date().getTime() - 1000L * 3600 * 24 * 5));
		testRun5Day.setEnd(new Date(testRun5Day.getStart().getTime() + 1000L * 3600));
		testRun5Day.setTestCases(testCases);
		repository.saveTestRun(testRun5Day);

		assertEquals(3, repository.getTestRuns(20).size());

		repository.cleanTestRunsOlderThan(3600L * 24 * 7);
		assertEquals(3, repository.getTestRuns(20).size());

		repository.cleanTestRunsOlderThan(3600L * 24 * 6);
		assertEquals(3, repository.getTestRuns(20).size());

		repository.cleanTestRunsOlderThan(3600L * 24 * 4);
		assertEquals(2, repository.getTestRuns(20).size());

		repository.cleanTestRunsOlderThan(3600L * 24 * 2);
		assertEquals(1, repository.getTestRuns(20).size());

		repository.cleanTestRunsOlderThan(3600L * 24 * 0);
		assertEquals(0, repository.getTestRuns(20).size());
	}

	@Test
	public void saveTestRun() {
		final Date endDate = new Date();
		final Date startDate = new Date(endDate.getTime() - 1000 * 60);

		assertEquals(0, repository.getTestRuns(20).size());

		TestCase tc1 = repository.newTestCase();
		tc1.setName("testCase1");
		tc1.setActual("helloA");
		tc1.setExpected("helloE");

		TestCase tc2 = repository.newTestCase();
		tc2.setName("testCase2");
		tc2.setActual("999a");
		tc2.setExpected("999a");

		List<TestCase> testCases = new ArrayList<>();
		testCases.add(tc1);
		testCases.add(tc2);

		TestRun toSave = repository.newTestRun();
		toSave.setStart(startDate);
		toSave.setEnd(endDate);
		toSave.setTestCases(testCases);

		toSave = repository.saveTestRun(toSave);

		assertEquals(1, repository.getTestRuns(20).size());
		assertEquals(2, repository.getTestRuns(20).get(0).getTestCases().size());

		assertEquals(startDate, repository.getTestRuns(20).get(0).getStart());
		assertEquals(endDate, repository.getTestRuns(20).get(0).getEnd());
		assertEquals(toSave.getId(), repository.getTestRuns(20).get(0).getId());
		assertEquals(TestResult.FAILED, repository.getTestRuns(20).get(0).getResult());

		final Map<String, TestCase> cases = new HashMap<>();
		for (TestCase cur : repository.getTestRuns(20).get(0).getTestCases())
			cases.put(cur.getName(), cur);

		assertEquals("helloA", cases.get("testCase1").getActual());
		assertEquals("helloE", cases.get("testCase1").getExpected());
		assertEquals(TestResult.FAILED, cases.get("testCase1").getResult());
		assertEquals("999a", cases.get("testCase2").getActual());
		assertEquals("999a", cases.get("testCase2").getExpected());
		assertEquals(TestResult.PASSED, cases.get("testCase2").getResult());

	}

	@Test
	public void testLimitValueTestRun() {
		assertEquals(0, repository.getTestRuns(20).size());

		TestCase tc1 = repository.newTestCase();
		tc1.setName("testCase1");
		tc1.setActual("hello");
		tc1.setExpected("hello");

		TestCase tc2 = repository.newTestCase();
		tc2.setName("testCase2");
		tc2.setActual("999");
		tc2.setExpected("999");

		List<TestCase> testCases = new ArrayList<>();
		testCases.add(tc1);
		testCases.add(tc2);

		for (int i = 0; i < 25; i++) {

			TestRun toSave = repository.newTestRun();
			toSave.setStart(new Date());
			toSave.setEnd(new Date());
			toSave.setTestCases(testCases);
			repository.saveTestRun(toSave);

		}

		assertEquals(20, repository.getTestRuns(20).size());
		assertEquals(25, repository.getTestRuns(30).size());

	}

	@Test(expected = IllegalArgumentException.class)
	public void saveTestRunWithEmptyTestCases() {
		TestRun toSave = repository.newTestRun();
		toSave.setStart(new Date());
		toSave.setEnd(new Date());
		toSave.setTestCases(new ArrayList<TestCase>());

		repository.saveTestRun(toSave);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveTestRunWithNullTestCases() {
		TestRun toSave = repository.newTestRun();
		toSave.setStart(new Date());
		toSave.setEnd(new Date());
		toSave.setTestCases(null);

		repository.saveTestRun(toSave);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveTestRunWithInvalidTestCases() {

		TestCase tc1 = repository.newTestCase();
		tc1.setName("testCase1");
		tc1.setActual("            ");
		tc1.setExpected("hello");

		List<TestCase> testCases = new ArrayList<>();
		testCases.add(tc1);

		TestRun toSave = repository.newTestRun();
		toSave.setStart(new Date());
		toSave.setEnd(new Date());
		toSave.setTestCases(testCases);

		repository.saveTestRun(toSave);
	}

	@Test(expected = IllegalArgumentException.class)
	public void cleanTestRunsOlderThanTooLowSeconds() {
		repository.cleanTestRunsOlderThan(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveTestRunWithConflictiveTestCases() {

		assertEquals(0, repository.getTestRuns(20).size());

		TestCase tc1 = repository.newTestCase();
		tc1.setName("sameName");
		tc1.setActual("differentActual");
		tc1.setExpected("hello");

		TestCase tc2 = repository.newTestCase();
		tc2.setName("sameName");
		tc2.setActual("xxxyyyyzzzz");
		tc2.setExpected("hello");

		List<TestCase> testCases = new ArrayList<>();
		testCases.add(tc1);
		testCases.add(tc2);

		TestRun toSave = repository.newTestRun();
		toSave.setStart(new Date());
		toSave.setEnd(new Date());
		toSave.setTestCases(testCases);

		repository.saveTestRun(toSave);

	}

}
