package model.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseFiltersCache {

	private static final long THREAD_WAIT_INTERVAL = 5000L; // in ms
	private static List<Filter> cachedFilters = new ArrayList<>();
	private static CountDownLatch isInitialized = new CountDownLatch(1);

	private DatabaseFiltersCache() {
	}

	public static List<Filter> findAllFilters() {
		try {

			// wait for cache to become initialized
			if (!isInitialized.await(THREAD_WAIT_INTERVAL, TimeUnit.MILLISECONDS))
				throw new IllegalArgumentException("filters cache is not refreshed yet");

			// serve request from cache
			return cachedFilters;

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void refreshCache() {
		DatabaseFilterRepository repository = new DatabaseFilterRepository();
		cachedFilters = repository.getAllFilters();
		isInitialized.countDown();
	}
}
