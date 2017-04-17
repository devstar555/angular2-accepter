package model.review;

class MemoryReviewReason extends ReviewReason {

	private Long id;

	MemoryReviewReason(Long id, String type, String value) {
		super(type, value);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
