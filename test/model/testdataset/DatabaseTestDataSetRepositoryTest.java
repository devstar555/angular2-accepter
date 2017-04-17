package model.testdataset;

import org.junit.After;
import org.junit.Before;

import play.Application;
import play.test.Helpers;

public class DatabaseTestDataSetRepositoryTest extends TestDataSetRepositoryTest {

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
	protected TestDataSetRepository newRepository() {
		return new DatabaseTestDataSetRepository();
	}

}
