package model.testrun;

import org.junit.After;
import org.junit.Before;

import play.Application;
import play.test.Helpers;

public class DatabaseTestRunRepositoryTest extends TestRunRepositoryTest {

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
	protected TestRunRepository newRepository() {
		return new DatabaseTestRunRepository();
	}

}
