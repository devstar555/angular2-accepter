package model.review;

import java.util.Date;
import java.util.List;

class MemoryReviewRequest extends ReviewRequest {

	private Long id;

	MemoryReviewRequest(Long id, Long orderId, Date requestDate, List<ReviewReason> reasons) {
		super(orderId, requestDate, reasons);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
