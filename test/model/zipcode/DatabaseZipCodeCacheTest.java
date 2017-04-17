package model.zipcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Application;
import play.test.Helpers;

public class DatabaseZipCodeCacheTest {

	private Application app;

	@Before
	public void before() {
		app = Helpers.fakeApplication();
	}

	@After
	public void after() {
		Helpers.stop(app);
	}

	@Test
	public void accessCacheDataBeforeRefresh() {
		ZipCode actual = DatabaseZipCodeCache.getZipCode("ch", "2222");
		assertNull(actual);
	}

	@Test
	public void accessCacheDataAfterReload() {
		ZipCode expected = new DatabaseZipCodeRepository().saveZipCode("1010", "Wien", "AT", "48.2077", "16.3705");
		DatabaseZipCodeCache.refreshCache();
		ZipCode actual = DatabaseZipCodeCache.getZipCode("AT", "1010");

		assertEquals(expected.getId(), actual.getId());
		assertEquals(expected.getZip(), actual.getZip());
		assertEquals(expected.getPlaceName(), actual.getPlaceName());
		assertEquals(expected.getCountryCode(), actual.getCountryCode());
		assertEquals(expected.getLatitude(), actual.getLatitude());
		assertEquals(expected.getLongitude(), actual.getLongitude());

		actual = DatabaseZipCodeCache.getZipCode("ch", "2222");
		assertNull(actual);
	}

}
