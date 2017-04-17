package model.testdataset;

class MemoryTestDataSet extends TestDataSet {

	MemoryTestDataSet() {
	}

	MemoryTestDataSet(Long timeoutMilliseconds, long[] expectAccept, long[] expectReview, long[] performance) {
		super(timeoutMilliseconds, expectAccept, expectReview, performance);
	}
}
