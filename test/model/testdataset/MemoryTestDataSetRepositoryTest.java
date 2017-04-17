package model.testdataset;

public class MemoryTestDataSetRepositoryTest extends TestDataSetRepositoryTest {

	@Override
	protected TestDataSetRepository newRepository() {
		return new MemoryTestDataSetRepository();
	}

}
