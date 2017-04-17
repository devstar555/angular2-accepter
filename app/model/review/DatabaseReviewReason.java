package model.review;

import db.DbReviewReason;

class DatabaseReviewReason extends ReviewReason {

	private Long id;

	DatabaseReviewReason(DbReviewReason reason) {
		super(reason.getType(), reason.getValue());
		this.id = reason.getId();
	}

	DatabaseReviewReason(String type, String value) {
		super(type, value);
	}

	@Override
	public Long getId() {
		return id;
	}

}
