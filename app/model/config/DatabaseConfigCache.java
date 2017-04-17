package model.config;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class DatabaseConfigCache {

	private static final long THREAD_WAIT_INTERVAL = 5000L; // in ms
	private static Map<String, Property> cachedProperties = new ConcurrentHashMap<>();
	private static CountDownLatch isInitialized = new CountDownLatch(1);

	private DatabaseConfigCache() {
	}

	public static String getValue(final String propertyName) {
		try {

			// wait for cache to become initialized
			if (!isInitialized.await(THREAD_WAIT_INTERVAL, TimeUnit.MILLISECONDS))
				throw new IllegalArgumentException("Config Cache is not refreshed yet");

			// serve request from cache
			Property property = cachedProperties.get(propertyName);

			return property != null ? property.getValue() : null;

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void refreshCache() {
		DatabaseConfigRepository repository = new DatabaseConfigRepository();
		List<Property> properties = repository.findAllProperties();
		Map<String, Property> newCachedProperties = new ConcurrentHashMap<>();
		for (Property property : properties)
			newCachedProperties.put(property.getName(), property);

		cachedProperties = newCachedProperties;
		isInitialized.countDown();
	}
}
