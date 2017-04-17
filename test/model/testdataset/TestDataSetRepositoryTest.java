package model.testdataset;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class TestDataSetRepositoryTest {

	protected abstract TestDataSetRepository newRepository();

	private TestDataSetRepository repository;

	private static final long TIME_OUT_IN_MS = 5000;
	private static final long[] EXPECT_ACCEPT_DATA = {};
	private static final long[] EXPECT_REVIEW_DATA = {
		2846779,
		3006528,
		3221238,
		3301777,
		3375103,
		5303407,
		5303836,
		5581034,
		5581129,
		5581171,
		5581350,
		5934582,
		6556027,
		6556042,
		6599322,
		7868600,
		8532752,
		9141049,
		9141136,
		10365625,
		11102769,
		11774959,
		13117858,
		14880982,
		14880990,
		14881306,
		15863955,
		16927130,
		16927161,
		17334848,
		17348518,
		17348553,
		17348757,
		17348922,
		18920369,
		18996047 };
	private static final long[] PERFORMANCE_DATA = {
		18818184,
		18823820,
		17025228,
		18814659,
		18828273,
		13185021,
		18407921,
		15682352 };

	private static void resetModelFactories() {
		TestDataSetRepositoryFactory.FACTORY = TestDataSetRepositoryFactory
				.factoryCaching(TestDataSetRepositoryFactory.factoryMemory());
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
	public void saveTestDataSet_minimalData() {

		assertNull(repository.getTestDataSet());

		TestDataSet testDataSet = repository.saveTestDataSet(0L, ArrayUtils.EMPTY_LONG_ARRAY,
				ArrayUtils.EMPTY_LONG_ARRAY, ArrayUtils.EMPTY_LONG_ARRAY);

		assertNotNull(repository.getTestDataSet());
		assertEquals(0L, testDataSet.getTimeoutMilliseconds());
		assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, testDataSet.getExpectAccept());
		assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, testDataSet.getExpectReview());
		assertArrayEquals(ArrayUtils.EMPTY_LONG_ARRAY, testDataSet.getPerformance());
	}

	@Test
	public void saveTestDataSet_someData() {

		assertNull(repository.getTestDataSet());

		long[] newExpectAcceptData = new long[] { 100 };
		long[] newExpectReviewData = new long[] { 200, 300 };
		long[] newExpectPerformance = new long[] { 400, 500, 600 };

		TestDataSet testDataSet = repository.saveTestDataSet(1000L, newExpectAcceptData, newExpectReviewData,
				newExpectPerformance);

		assertNotNull(repository.getTestDataSet());
		assertEquals(1000L, testDataSet.getTimeoutMilliseconds());
		assertArrayEquals(newExpectAcceptData, testDataSet.getExpectAccept());
		assertArrayEquals(newExpectReviewData, testDataSet.getExpectReview());
		assertArrayEquals(newExpectPerformance, testDataSet.getPerformance());
	}

	@Test
	public void saveTestDataSet() {

		assertNull(repository.getTestDataSet());

		repository.saveTestDataSet(TIME_OUT_IN_MS, EXPECT_ACCEPT_DATA, EXPECT_REVIEW_DATA, PERFORMANCE_DATA);

		assertNotNull(repository.getTestDataSet());

		long[] newExpectAcceptData = new long[] { 100 };
		long[] newExpectReviewData = new long[] { 200, 300 };
		long[] newExpectPerformance = new long[] { 400, 500, 600 };

		TestDataSet updatedTestDataSet = repository.saveTestDataSet(10000L, newExpectAcceptData, newExpectReviewData,
				newExpectPerformance);

		assertNotNull(repository.getTestDataSet());
		assertEquals(10000, updatedTestDataSet.getTimeoutMilliseconds());
		assertArrayEquals(newExpectAcceptData, updatedTestDataSet.getExpectAccept());
		assertArrayEquals(newExpectReviewData, updatedTestDataSet.getExpectReview());
		assertArrayEquals(newExpectPerformance, updatedTestDataSet.getPerformance());
	}

	@Test
	public void getTestDataSet() {

		assertNull(repository.getTestDataSet());

		repository.saveTestDataSet(TIME_OUT_IN_MS, EXPECT_ACCEPT_DATA, EXPECT_REVIEW_DATA, PERFORMANCE_DATA);

		TestDataSet response = repository.getTestDataSet();

		assertNotNull(response);
		assertEquals(TIME_OUT_IN_MS, response.getTimeoutMilliseconds());
		assertArrayEquals(EXPECT_ACCEPT_DATA, response.getExpectAccept());
		assertArrayEquals(EXPECT_REVIEW_DATA, response.getExpectReview());
		assertArrayEquals(PERFORMANCE_DATA, response.getPerformance());
	}
}
