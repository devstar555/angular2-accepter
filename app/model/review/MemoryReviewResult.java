package model.review;

import java.util.Date;

class MemoryReviewResult extends ReviewResult {

	private Long id;

	MemoryReviewResult(Long id, Long orderId, String action, Date dateAction) {
		super(orderId, action, dateAction);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
