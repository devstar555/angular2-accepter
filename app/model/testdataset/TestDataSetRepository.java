package model.testdataset;

public interface TestDataSetRepository {
	TestDataSet saveTestDataSet(long timeoutMilliseconds, long[] expectAccept, long[] expectReview, long[] performance);
	TestDataSet getTestDataSet();
}
