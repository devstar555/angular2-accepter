package model.testdataset;

import java.util.ArrayList;
import java.util.List;

class MemoryTestDataSetRepository implements TestDataSetRepository {

	private List<TestDataSet> values = new ArrayList<>();

	MemoryTestDataSetRepository() {
	}

	@Override
	public TestDataSet saveTestDataSet(long timeoutMilliseconds, long[] expectAccept, long[] expectReview,
			long[] performance) {

		// check if it exists
		TestDataSet existed = values.stream().findFirst().orElse(null);

		if (existed == null) {
			existed = new MemoryTestDataSet();
			values.add(existed);
		}

		existed.setExpectAccept(expectAccept);
		existed.setExpectReview(expectReview);
		existed.setPerformance(performance);
		existed.setTimeoutMilliseconds(timeoutMilliseconds);
		return existed;
	}

	@Override
	public TestDataSet getTestDataSet() {
		return values.stream().findFirst().orElse(null);
	}
}
