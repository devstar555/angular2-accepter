package model.testdataset;

import java.util.List;

import db.DbTestDataSet;
import util.Utils;

class DatabaseTestDataSetRepository implements TestDataSetRepository {

	DatabaseTestDataSetRepository() {
	}

	@Override
	public TestDataSet saveTestDataSet(long timeoutMilliseconds, long[] expectAccept, long[] expectReview,
			long[] performance) {

		// check if it exists
		List<DbTestDataSet> allTestDataSets = DbTestDataSet.FINDER.all();

		DbTestDataSet testDataSet = !allTestDataSets.isEmpty() ? allTestDataSets.get(0) : new DbTestDataSet();
		testDataSet.setExpectAccept(Utils.longArrayToString(expectAccept));
		testDataSet.setExpectReview(Utils.longArrayToString(expectReview));
		testDataSet.setPerformance(Utils.longArrayToString(performance));
		testDataSet.setTimeoutMilliseconds(timeoutMilliseconds);
		testDataSet.save();
		return new DatabaseTestDataSet(testDataSet);
	}

	@Override
	public TestDataSet getTestDataSet() {
		DbTestDataSet dbTestDataSet = DbTestDataSet.FINDER.setMaxRows(1).findUnique();
		return dbTestDataSet != null ? (new DatabaseTestDataSet(dbTestDataSet)) : null;
	}
}
