package model.filter;

import org.junit.After;
import org.junit.Before;

import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.platformaccountgroup.TestPlatformAccountGroupCreator;
import model.zipcode.ZipCodeRepositoryFactory;
import play.Application;
import play.test.Helpers;

public class DatabaseFilterRepositoryTest extends FilterRepositoryTest {
	private Application app;
	private Long[] ids;

	private static void resetModelFactories() {
		PlatformAccountGroupRepositoryFactory.FACTORY = PlatformAccountGroupRepositoryFactory
				.factoryCaching(PlatformAccountGroupRepositoryFactory.factoryDatabase());
		ZipCodeRepositoryFactory.FACTORY = ZipCodeRepositoryFactory
				.factoryCaching(ZipCodeRepositoryFactory.factoryDatabase());
	}

	@Before
	public void before() {
		resetModelFactories();
		app = Helpers.fakeApplication();
		refreshCaches();
		ids = new Long[] { TestPlatformAccountGroupCreator.createPlatformAccountGroup().getId() };
		createTestZipCodes();
		TestLoginUserCeator.createHttpBasicAuth();
	}

	@After
	public void after() {
		Helpers.stop(app);
	}

	@Override
	protected FilterRepository newRepository() {
		return new DatabaseFilterRepository();
	}
	@Override
	protected Long[] getPlatformAccountGroupIds() {
		return ids;
	}

	@Override
	protected void refreshCaches() {
		DatabaseFiltersCache.refreshCache();
	}
}
