package model.review;

import java.util.Date;

public abstract class ReviewResult {
	private Long orderId;
	private String action;
	private Date dateAction;

	ReviewResult(Long orderId, String action, Date dateAction) {
		super();
		this.orderId = orderId;
		this.action = action;
		this.dateAction = dateAction;
	}

	ReviewResult() {

	}

	public abstract Long getId();

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Date getDateAction() {
		return dateAction;
	}

	public void setDateAction(Date dateAction) {
		this.dateAction = dateAction;
	}
}
