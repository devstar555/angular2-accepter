package model.review;

public class MemoryReviewRepositoryTest extends ReviewRepositoryTest {

	@Override
	protected ReviewRepository newRepository() {
		return new MemoryReviewRepository();
	}

}