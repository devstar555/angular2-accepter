package model.testdataset;

import db.DbTestDataSet;
import util.Utils;

class DatabaseTestDataSet extends TestDataSet {

	DatabaseTestDataSet() {
	}

	DatabaseTestDataSet(DbTestDataSet testDataSet) {
		super(testDataSet.getTimeoutMilliseconds(), Utils.stringToLongArray(testDataSet.getExpectAccept()),
				Utils.stringToLongArray(testDataSet.getExpectReview()),
				Utils.stringToLongArray(testDataSet.getPerformance()));
	}
}
