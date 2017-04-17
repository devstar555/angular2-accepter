package model.config;

import org.junit.After;
import org.junit.Before;

import play.Application;
import play.test.Helpers;

public class DatabaseConfigRepositoryTest extends ConfigRepositoryTest {

	private Application app;

	@Before
	public void before() {
		app = Helpers.fakeApplication();
	}

	@After
	public void after() {
		Helpers.stop(app);
	}

	@Override
	protected ConfigRepository newRepository() {
		return new DatabaseConfigRepository();
	}

	@Override
	protected void refreshCaches() {
		DatabaseConfigCache.refreshCache();
	}
}
