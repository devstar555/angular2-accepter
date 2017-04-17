package model.testrun;

import db.DbTestCase;

class DatabaseTestCase extends TestCase {

	private Long id;

	@Override
	public Long getId() {
		return id;
	}
	DatabaseTestCase() {
	}

	DatabaseTestCase(DbTestCase testCase) {
		super(testCase.getName(), testCase.getActual(), testCase.getExpected());
		this.id = testCase.getId();

	}

}
