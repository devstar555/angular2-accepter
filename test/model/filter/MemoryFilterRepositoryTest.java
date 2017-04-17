package model.filter;

import org.junit.Before;

import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.platformaccountgroup.TestPlatformAccountGroupCreator;
import model.zipcode.ZipCodeRepositoryFactory;

public class MemoryFilterRepositoryTest extends FilterRepositoryTest {

	private Long[] ids;
	
	private static void resetModelFactories() {
		PlatformAccountGroupRepositoryFactory.FACTORY = PlatformAccountGroupRepositoryFactory
				.factoryCaching(PlatformAccountGroupRepositoryFactory.factoryMemory());
		ZipCodeRepositoryFactory.FACTORY = ZipCodeRepositoryFactory
				.factoryCaching(ZipCodeRepositoryFactory.factoryMemory());
	}

	@Before
	public void before() {
		resetModelFactories();
		ids = new Long[] { TestPlatformAccountGroupCreator.createPlatformAccountGroup().getId() };
		createTestZipCodes();
		TestLoginUserCeator.createHttpBasicAuth();
	}

	@Override
	protected FilterRepository newRepository() {
		return new MemoryFilterRepository();
	}
	@Override
	protected Long[] getPlatformAccountGroupIds() {
		return ids;
	}
}
