package model.platformaccountgroup;

import org.junit.After;
import org.junit.Before;

import model.time.TimeProviderFactory;
import play.Application;
import play.test.Helpers;

public class DatabasePlatformAccountGroupRepositoryTest extends PlatformAccountGroupRepositoryTest {

	private Application app;

	private static void resetModelFactories() {
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(0L);
	}

	@Before
	public void before() {
		resetModelFactories();
		app = Helpers.fakeApplication();
	}

	@After
	public void after() {
		Helpers.stop(app);
		resetModelFactories();
	}

	@Override
	protected DatabasePlatformAccountGroupRepository newRepository() {
		return new DatabasePlatformAccountGroupRepository();
	}
}
