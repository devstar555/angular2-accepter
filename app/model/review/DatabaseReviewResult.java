package model.review;

import db.DbReviewResult;

class DatabaseReviewResult extends ReviewResult {

	private Long id;

	DatabaseReviewResult(DbReviewResult result) {
		super(result.getOrderId(), result.getAction(), result.getActionDate());
		this.id = result.getId();
	}

	@Override
	public Long getId() {
		return id;
	}
}
