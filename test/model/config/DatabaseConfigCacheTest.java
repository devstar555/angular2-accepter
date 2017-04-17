package model.config;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import play.Application;
import play.test.Helpers;

public class DatabaseConfigCacheTest {

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
	public void testAccessCacheData() {
		DatabaseConfigCache.refreshCache();
		String value = DatabaseConfigCache.getValue("ZIP_MAX_DISTANCE");
		assertEquals("50000", value);
		new DatabaseConfigRepository().setValue("ZIP_MAX_DISTANCE", "40000");
		DatabaseConfigCache.refreshCache();
		value = DatabaseConfigCache.getValue("ZIP_MAX_DISTANCE");
		assertEquals("40000", value);
	}
}
