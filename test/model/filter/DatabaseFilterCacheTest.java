package model.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.platformaccountgroup.PlatformAccountGroup;
import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.platformaccountgroup.TestPlatformAccountGroupCreator;
import play.Application;
import play.test.Helpers;

public class DatabaseFilterCacheTest {

	private Application app;

	@Before
	public void before() {
		app = Helpers.fakeApplication();
		PlatformAccountGroupRepositoryFactory.FACTORY = PlatformAccountGroupRepositoryFactory
				.factoryCaching(PlatformAccountGroupRepositoryFactory.factoryDatabase());
		FilterRepositoryFactory.FACTORY = FilterRepositoryFactory
				.factoryCaching(FilterRepositoryFactory.factoryDatabase());
		TestLoginUserCeator.createHttpBasicAuth();
	}

	@After
	public void after() {
		Helpers.stop(app);
	}

	@Test
	public void accessCacheDataBeforeRefresh() {
		List<Filter> actual = DatabaseFiltersCache.findAllFilters();
		assertEquals(0, actual.size());
	}

	@Test
	public void accessCacheDataAfterReload() {
		PlatformAccountGroup group = TestPlatformAccountGroupCreator.createPlatformAccountGroup();
		FilterRepositoryFactory.get().saveCompanyFilter("company", "AT", "1010", "test", new Long[] { group.getId() });

		DatabaseFiltersCache.refreshCache();
		List<Filter> actual = DatabaseFiltersCache.findAllFilters();

		assertEquals(1, actual.size());
		assertTrue(actual.get(0).getId() != null);
		assertEquals("company", ((CompanyFilter) actual.get(0)).getName());
		assertEquals("AT", ((CompanyFilter) actual.get(0)).getCountry());
		assertEquals("1010", ((CompanyFilter) actual.get(0)).getZip());
		assertEquals("test", ((CompanyFilter) actual.get(0)).getDescription());
		assertEquals(1, ((CompanyFilter) actual.get(0)).getPlatformAccountGroupIds().length);
		assertEquals(group.getId(), ((CompanyFilter) actual.get(0)).getPlatformAccountGroupIds()[0]);

		FilterRepositoryFactory.get().deleteCompanyFilter(actual.get(0).getId());
		DatabaseFiltersCache.refreshCache();
		actual = DatabaseFiltersCache.findAllFilters();
		assertEquals(0, actual.size());

	}
}
