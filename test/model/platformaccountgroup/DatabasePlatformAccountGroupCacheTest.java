package model.platformaccountgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Application;
import play.test.Helpers;

public class DatabasePlatformAccountGroupCacheTest {

	private Application app;

	@Before
	public void before() {
		app = Helpers.fakeApplication();
		DatabasePlatformAccountGroupCache.refreshCache();
	}

	@After
	public void after() {
		Helpers.stop(app);
	}

	@Test
	public void accessCacheWithoutDataCreation() {
		PlatformAccountGroup actual = DatabasePlatformAccountGroupCache.findById(1L);
		assertNull(actual);
	}

	@Test
	public void accessCacheAfterDataCreation() {
		new DatabasePlatformAccountGroupRepository().savePlatformAccountGroup("test", "test desc",
				new String[] { "amazon" });
		DatabasePlatformAccountGroupCache.refreshCache();
		PlatformAccountGroup actual = DatabasePlatformAccountGroupCache.findById(1L);

		assertEquals(1, actual.getId().longValue());
		assertEquals("test", actual.getName());
		assertEquals("test desc", actual.getDescription());
		assertEquals(1, actual.getAccounts().length);
		assertEquals("amazon", actual.getAccounts()[0]);
	}
}
