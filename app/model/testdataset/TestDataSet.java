package model.testdataset;

public abstract class TestDataSet {

	private long timeoutMilliseconds;
	private long[] expectAccept;
	private long[] expectReview;
	private long[] performance;

	TestDataSet() {
	}

	TestDataSet(long timeoutMilliseconds, long[] expectAccept, long[] expectReview, long[] performance) {
		this.timeoutMilliseconds = timeoutMilliseconds;
		this.expectAccept = expectAccept;
		this.expectReview = expectReview;
		this.performance = performance;
	}

	public long getTimeoutMilliseconds() {
		return timeoutMilliseconds;
	}

	public void setTimeoutMilliseconds(long timeoutMilliseconds) {
		this.timeoutMilliseconds = timeoutMilliseconds;
	}

	public long[] getExpectAccept() {
		return expectAccept;
	}

	public void setExpectAccept(long[] expectAccept) {
		this.expectAccept = expectAccept;
	}

	public long[] getExpectReview() {
		return expectReview;
	}

	public void setExpectReview(long[] expectReview) {
		this.expectReview = expectReview;
	}

	public long[] getPerformance() {
		return performance;
	}

	public void setPerformance(long[] performance) {
		this.performance = performance;
	}

}
