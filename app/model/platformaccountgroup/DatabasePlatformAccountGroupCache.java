package model.platformaccountgroup;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabasePlatformAccountGroupCache {

	private static final long THREAD_WAIT_INTERVAL = 5000L; // in ms
	private static Map<Long, PlatformAccountGroup> cachedPlatformAccountGroups = new ConcurrentHashMap<>();
	private static CountDownLatch isInitialized = new CountDownLatch(1);

	private DatabasePlatformAccountGroupCache() {
	}

	public static PlatformAccountGroup findById(Long id) {
		try {

			// wait for cache to become initialized
			if (!isInitialized.await(THREAD_WAIT_INTERVAL, TimeUnit.MILLISECONDS))
				throw new IllegalArgumentException("platformAccountGroup cache is not refreshed yet");

			// serve request from cache
			return cachedPlatformAccountGroups.get(id);

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void refreshCache() {
		DatabasePlatformAccountGroupRepository repository = new DatabasePlatformAccountGroupRepository();
		List<PlatformAccountGroup> platformAccountGroups = repository.findAllPlatformAccountGroups();
		final Map<Long, PlatformAccountGroup> newCachedPlatformAccountGroups = new ConcurrentHashMap<>();
		platformAccountGroups.stream().forEach(group -> newCachedPlatformAccountGroups.put(group.getId(), group));
		cachedPlatformAccountGroups = newCachedPlatformAccountGroups;
		isInitialized.countDown();
	}
}
