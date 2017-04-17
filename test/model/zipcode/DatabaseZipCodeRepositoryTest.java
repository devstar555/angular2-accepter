package model.zipcode;

import org.junit.After;
import org.junit.Before;

import play.Application;
import play.test.Helpers;

public class DatabaseZipCodeRepositoryTest extends ZipCodeRepositoryTest {

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
	protected ZipCodeRepository newRepository() {
		return new DatabaseZipCodeRepository();
	}

	@Override
	protected void refreshCaches() {
		DatabaseZipCodeCache.refreshCache();
	}
}
