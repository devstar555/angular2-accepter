package model.testrun;

public class MemoryTestRunRepositoryTest extends TestRunRepositoryTest {

	@Override
	protected TestRunRepository newRepository() {
		return new MemoryTestRunRepository();
	}

}
