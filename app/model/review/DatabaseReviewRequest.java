package model.review;

import db.DbReviewRequest;
import util.Utils;

class DatabaseReviewRequest extends ReviewRequest {

	private Long id;

	DatabaseReviewRequest(DbReviewRequest request) {
		super(request.getOrderId(), request.getRequestDate(),
				Utils.mapList(request.getReasons(), x -> new DatabaseReviewReason(x)));
		this.id = request.getId();
	}

	@Override
	public Long getId() {
		return id;
	}

}
