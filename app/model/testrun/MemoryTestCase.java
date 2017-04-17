package model.testrun;

class MemoryTestCase extends TestCase {

	private Long id;

	MemoryTestCase() {
	}

	MemoryTestCase(long id, TestCase test) {
		super(test.getName(), test.getActual(), test.getExpected());
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
