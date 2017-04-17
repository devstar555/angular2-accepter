package model.review;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import db.DbReviewReason;
import model.config.Config;
import model.time.TimeProviderFactory;
import play.Application;
import play.test.Helpers;

public class DatabaseReviewRepositoryTest extends ReviewRepositoryTest {

	private Application app;

	private static void resetModelFactories() {
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(0L);
	}

	@Before
	public void before() {
		resetModelFactories();
		app = Helpers.fakeApplication();
	}

	@After
	public void after() {
		Helpers.stop(app);
		resetModelFactories();
	}

	@Override
	protected ReviewRepository newRepository() {
		return new DatabaseReviewRepository();
	}

	@Test
	public void cleanReasons() {
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

		assertEquals(3, DbReviewReason.FINDER.all().size());
		repository.cleanReviewsOlderThan(3600L * 24 * 9);
		assertEquals(3, DbReviewReason.FINDER.all().size());
		repository.cleanReviewsOlderThan(3600L * 24 * 7);
		assertEquals(2, DbReviewReason.FINDER.all().size());
		repository.cleanReviewsOlderThan(3600L * 24 * 5);
		assertEquals(1, DbReviewReason.FINDER.all().size());
		repository.cleanReviewsOlderThan(3600L * 24 * 3);
		assertEquals(1, DbReviewReason.FINDER.all().size());
		repository.cleanReviewsOlderThan(3600L * 24 * 1);
		assertEquals(1, DbReviewReason.FINDER.all().size());
		repository.cleanReviewsOlderThan(3600L * 24 * -1);
		assertEquals(0, DbReviewReason.FINDER.all().size());
	}

}
