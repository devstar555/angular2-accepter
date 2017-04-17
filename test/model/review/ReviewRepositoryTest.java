package model.review;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.config.Config;
import model.config.ConfigRepositoryFactory;
import model.time.TimeProviderFactory;

public abstract class ReviewRepositoryTest {

	protected abstract ReviewRepository newRepository();

	protected ReviewRepository repository;

	private static void resetModelFactories() {
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(0L);
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
	public void safeFind_reviewResult() {
		repository.saveReviewResult(5544L, "ACCEPT");
		assertEquals(1, repository.findAllReviewResults().size());
		repository.findAllReviewResults().clear();
		assertEquals(1, repository.findAllReviewResults().size());
	}

	@Test
	public void cleanManualReviews() {
		Config.setCleanData(-1000L * 3600 * 24 * 30);

		final ReviewReason reasonA = repository.newReviewReason("typeA", "valueA");
		final ReviewReason reasonB = repository.newReviewReason("typeB", "valueB");
		final ReviewReason reasonC = repository.newReviewReason("typeC", "valueC");

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 24 * 10);
		repository.saveReviewRequest(1L, Arrays.asList(reasonA));
		repository.saveReviewResult(1L, "ACCEPT");

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 24 * 8);
		repository.saveReviewRequest(2L, Arrays.asList(reasonA, reasonB));
		repository.saveReviewResult(2L, "REJECT");

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 24 * 6);
		repository.saveReviewRequest(3L, Arrays.asList(reasonC));
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 24 * 4);
		repository.saveReviewResult(3L, "ACCEPT");

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 24 * 2);
		repository.saveReviewResult(4L, "REJECT");

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 24 * 0);
		repository.saveReviewRequest(5L, Arrays.asList(reasonB));

		assertEquals(4, repository.findAllReviewRequests().size());
		assertEquals(4, repository.findAllReviewResults().size());

		repository.cleanReviewsOlderThan(3600L * 24 * 11);
		assertEquals(4, repository.findAllReviewRequests().size());
		assertEquals(4, repository.findAllReviewResults().size());

		repository.cleanReviewsOlderThan(3600L * 24 * 9);
		assertEquals(3, repository.findAllReviewRequests().size());
		assertEquals(3, repository.findAllReviewResults().size());

		repository.cleanReviewsOlderThan(3600L * 24 * 7);
		assertEquals(2, repository.findAllReviewRequests().size());
		assertEquals(2, repository.findAllReviewResults().size());

		repository.cleanReviewsOlderThan(3600L * 24 * 5);
		assertEquals(1, repository.findAllReviewRequests().size());
		assertEquals(1, repository.findAllReviewResults().size());

		repository.cleanReviewsOlderThan(3600L * 24 * 3);
		assertEquals(1, repository.findAllReviewRequests().size());
		assertEquals(1, repository.findAllReviewResults().size());

		repository.cleanReviewsOlderThan(3600L * 24 * 1);
		assertEquals(1, repository.findAllReviewRequests().size());
		assertEquals(0, repository.findAllReviewResults().size());

		repository.cleanReviewsOlderThan(3600L * 24 * -1);
		assertEquals(0, repository.findAllReviewRequests().size());
		assertEquals(0, repository.findAllReviewResults().size());
	}

	@Test
	public void safeFind_reviewRequest() {
		repository.saveReviewRequest(5544L, Arrays.asList(repository.newReviewReason("type", "value")));
		assertEquals(1, repository.findAllReviewRequests().size());
		repository.findAllReviewRequests().clear();
		assertEquals(1, repository.findAllReviewRequests().size());
	}

	@Test
	public void safeFind_reviewResultTimeFilter() {
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-3000L);
		repository.saveReviewResult(5541L, "ACCEPT");
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L);
		repository.saveReviewResult(5542L, "ACCEPT");
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-5000L);
		repository.saveReviewResult(5543L, "ACCEPT");

		assertEquals(3, repository.findReviewResultsTimeFilter(6).size());
		assertEquals(2, repository.findReviewResultsTimeFilter(4).size());
		assertEquals(1, repository.findReviewResultsTimeFilter(2).size());
		assertEquals(0, repository.findReviewResultsTimeFilter(0).size());
	}

	@Test
	public void safeFind_reviewRequestTimeFilter() {
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-3000L);
		repository.saveReviewRequest(5541L, Arrays.asList(repository.newReviewReason("type", "value")));
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L);
		repository.saveReviewRequest(5542L, Arrays.asList(repository.newReviewReason("type", "value")));
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-5000L);
		repository.saveReviewRequest(5543L, Arrays.asList(repository.newReviewReason("type", "value")));

		assertEquals(3, repository.findReviewRequestsTimeFilter(6).size());
		assertEquals(2, repository.findReviewRequestsTimeFilter(4).size());
		assertEquals(1, repository.findReviewRequestsTimeFilter(2).size());
		assertEquals(0, repository.findReviewRequestsTimeFilter(0).size());
	}

	@Test
	public void saveAndRead_reviewRequest() {
		final ReviewReason reasonA = repository.newReviewReason("suspicious", "fischer");
		final ReviewReason reasonB = repository.newReviewReason("amount", "25.00");

		assertEquals(0, repository.findAllReviewRequests().size());
		final ReviewRequest saved = repository.saveReviewRequest(5544L, Arrays.asList(reasonA, reasonB));
		assertEquals(1, repository.findAllReviewRequests().size());

		final ReviewRequest request = repository.findAllReviewRequests().get(0);
		assertEquals(saved.getId(), request.getId());
		assertNotNull(request.getId());
		assertTrue(timeDiff(request.getRequestDate()) < 5000);
		assertEquals((Long) 5544L, request.getOrderId());
		assertEquals(2, request.getReviewReasons().size());
		assertTrue(containsReason(request, "suspicious", "fischer"));
		assertTrue(containsReason(request, "amount", "25.00"));

		for (ReviewReason curReason : request.getReviewReasons())
			assertNotNull(curReason.getId());

	}

	@Test
	public void saveAndRead_reviewResult() {
		assertEquals(0, repository.findAllReviewResults().size());
		final ReviewResult saved = repository.saveReviewResult(4488L, "ACCEPT");
		assertEquals(1, repository.findAllReviewResults().size());

		final ReviewResult result = repository.findAllReviewResults().get(0);
		assertEquals(saved.getId(), result.getId());
		assertNotNull(result.getId());
		assertEquals("ACCEPT", result.getAction());
		assertTrue(timeDiff(result.getDateAction()) < 5000);
		assertEquals((Long) 4488L, result.getOrderId());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveReviewResult_nullOrderId() {
		repository.saveReviewResult(null, "ACCEPT");
	}

	@Test(expected = IllegalArgumentException.class)
	public void cleanReviewsSecondsTooLow() {
		repository.cleanReviewsOlderThan(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveReviewResult_nullReason() {
		repository.saveReviewResult(5544L, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveReviewResult_emptyReason() {
		repository.saveReviewResult(5544L, "  	  ");
	}

	@Test
	public void saveReviewResult_cleanAction() {
		repository.saveReviewResult(1234L, "	PADDED  ");
		assertEquals("PADDED", repository.findAllReviewResults().get(0).getAction());
	}

	@Test
	public void saveReviewResult_overwriteExisting() throws Exception {

		assertEquals(0, repository.findAllReviewResults().size());

		repository.saveReviewResult(10L, "ACCEPT");
		assertEquals(1, repository.findAllReviewResults().size());
		final ReviewResult initial = repository.findAllReviewResults().get(0);
		assertEquals("ACCEPT", initial.getAction());
		final Date firstActionDate = initial.getDateAction();

		Thread.sleep(1); // force different timestamps
		repository.saveReviewResult(10L, "REJECT");
		assertEquals(1, repository.findAllReviewResults().size());
		final ReviewResult updated = repository.findAllReviewResults().get(0);
		assertEquals("REJECT", updated.getAction());
		assertNotEquals(firstActionDate, updated.getDateAction());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveReviewRequest_nullOrderId() {
		repository.saveReviewRequest(null, Arrays.asList(repository.newReviewReason("t", "v")));
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveReviewRequest_nullList() {
		repository.saveReviewRequest(5544L, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveReviewRequest_emptyList() {
		repository.saveReviewRequest(5544L, Collections.emptyList());
	}

	@Test(expected = IllegalArgumentException.class)
	public void newReason_nullType() {
		repository.newReviewReason(null, "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void newReason_emptyType() {
		repository.newReviewReason(" 	  ", "value");
	}

	@Test
	public void newReason_nullValue() {
		assertNull(repository.newReviewReason("type", null).getValue());
	}

	@Test
	public void newReason_emptyValue() {
		assertNull(repository.newReviewReason("type", " 	  ").getValue());
	}

	@Test
	public void saveReviewRequest_cleanList() {
		final ReviewReason r1A = repository.newReviewReason("suspicious", "fischer");
		final ReviewReason r1B = repository.newReviewReason("suspicious", "  fischer   ");
		final ReviewReason r2A = repository.newReviewReason("suspicious", null);
		final ReviewReason r2B = repository.newReviewReason("suspicious", "   ");
		final ReviewReason r3 = repository.newReviewReason("amount", "fischer");
		final ReviewRequest request = repository.saveReviewRequest(5544L, Arrays.asList(r1A, r1B, r2A, r2B, r3));
		assertEquals(3, request.getReviewReasons().size());
		assertTrue(containsReason(request, "suspicious", "fischer"));
		assertTrue(containsReason(request, "suspicious", null));
		assertTrue(containsReason(request, "amount", "fischer"));
	}

	@Test
	public void saveReviewRequest_multipleSaves() {

		assertEquals(0, repository.findAllReviewRequests().size());

		final ReviewReason aR1 = repository.newReviewReason("susp", "fischer");
		final ReviewReason aR2 = repository.newReviewReason("susp", null);
		final ReviewRequest a = repository.saveReviewRequest(111L, Arrays.asList(aR1, aR2));

		final ReviewReason bR1 = repository.newReviewReason("susp", "fischer");
		final ReviewReason bR2 = repository.newReviewReason("other", null);
		final ReviewReason bR3 = repository.newReviewReason("other", "withValue");
		final ReviewRequest b = repository.saveReviewRequest(222L, Arrays.asList(bR1, bR2, bR3));

		final Map<Long, ReviewRequest> retrieved = repository.findAllReviewRequests().stream()
				.collect(Collectors.toMap(x -> x.getId(), x -> x));

		assertEquals(2, retrieved.size());

		final ReviewRequest rA = retrieved.get(a.getId());
		assertEquals((Long) 111L, rA.getOrderId());
		assertEquals(2, rA.getReviewReasons().size());
		assertTrue(containsReason(rA, "susp", "fischer"));
		assertTrue(containsReason(rA, "susp", null));

		final ReviewRequest rB = retrieved.get(b.getId());
		assertEquals((Long) 222L, rB.getOrderId());
		assertEquals(3, rB.getReviewReasons().size());
		assertTrue(containsReason(rB, "susp", "fischer"));
		assertTrue(containsReason(rB, "other", null));
		assertTrue(containsReason(rB, "other", "withValue"));

	}

	@Test
	public void saveReviewRequest_overwriteExisting() throws Exception {
		assertEquals(0, repository.findAllReviewRequests().size());

		final ReviewReason r1 = repository.newReviewReason("susp", "fischer");
		final ReviewReason r2 = repository.newReviewReason("susp", "bader");
		repository.saveReviewRequest(100L, Arrays.asList(r1, r2));
		assertEquals(1, repository.findAllReviewRequests().size());
		final ReviewRequest initial = repository.findAllReviewRequests().get(0);
		final Date initialDate = initial.getRequestDate();

		Thread.sleep(1); // force different timestamps
		final ReviewReason r3 = repository.newReviewReason("susp", "other");
		repository.saveReviewRequest(100L, Arrays.asList(r1, r3));
		assertEquals(1, repository.findAllReviewRequests().size());
		final ReviewRequest updated = repository.findAllReviewRequests().get(0);
		assertEquals((Long) 100L, updated.getOrderId());
		assertNotEquals(initialDate, updated.getRequestDate());
		assertEquals(2, updated.getReviewReasons().size());
		containsReason(updated, "susp", "fischer");
		containsReason(updated, "susp", "other");
	}

	private static boolean containsReason(ReviewRequest request, String type, String value) {
		for (ReviewReason curReason : request.getReviewReasons()) {
			if (ObjectUtils.compare(curReason.getType(), type) == 0
					&& ObjectUtils.compare(curReason.getValue(), value) == 0) {
				return true;
			}
		}
		return false;
	}

	private static long timeDiff(Date date) {
		return new Date().getTime() - date.getTime();
	}

}