package model.review;

import java.util.Date;
import java.util.List;

public abstract class ReviewRequest {
	private Long orderId;
	private Date requestDate;
	private List<ReviewReason> reviewReasons;

	ReviewRequest(Long orderId, Date requestDate, List<ReviewReason> reviewReasons) {
		super();
		this.orderId = orderId;
		this.requestDate = requestDate;
		this.reviewReasons = reviewReasons;
	}

	ReviewRequest() {

	}

	public abstract Long getId();

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public List<ReviewReason> getReviewReasons() {
		return reviewReasons;
	}

	public void setReasons(List<ReviewReason> reasons) {
		this.reviewReasons = reasons;
	}
}
