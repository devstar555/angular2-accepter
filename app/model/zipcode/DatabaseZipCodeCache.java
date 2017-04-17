package model.zipcode;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class DatabaseZipCodeCache {

	private static final long THREAD_WAIT_INTERVAL = 5000L; // in ms
	private static Map<String, ZipCode> cachedZipCodes = new ConcurrentHashMap<>();
	private static CountDownLatch isInitialized = new CountDownLatch(1);
	protected static final Pattern NUMBERS_PATTERN = Pattern.compile("[^+0-9]");

	private DatabaseZipCodeCache() {
	}

	public static ZipCode getZipCode(final String countryCode, final String zip) {
		try {

			// wait for cache to become initialized
			if (!isInitialized.await(THREAD_WAIT_INTERVAL, TimeUnit.MILLISECONDS))
				throw new IllegalArgumentException("zip codes Cache is not refreshed yet");

			// serve request from cache
			return cachedZipCodes.get(getKey(countryCode, zip));

		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public static void refreshCache() {
		DatabaseZipCodeRepository repository = new DatabaseZipCodeRepository();
		List<ZipCode> zipCodes = repository.findAll();
		Map<String, ZipCode> newCachedZipCodes = new ConcurrentHashMap<>();
		for (ZipCode zipCode : zipCodes) {
			String zip = zipCode.getZip();
			String numericZip = NUMBERS_PATTERN.matcher(zip).replaceAll(StringUtils.EMPTY);
			zipCode.setZip(numericZip);
			String key = getKey(zipCode.getCountryCode(), numericZip);
			newCachedZipCodes.put(key, zipCode);
		}

		cachedZipCodes = newCachedZipCodes;
		isInitialized.countDown();
	}

	private static String getKey(String countryCode, String zip) {
		return countryCode.toUpperCase() + ":" + zip;
	}

}
