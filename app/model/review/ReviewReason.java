package model.review;

import org.apache.commons.lang3.ObjectUtils;

public abstract class ReviewReason {

	public static int compareValues(ReviewReason rA, ReviewReason rB) {
		if (rA == null && rB == null)
			return 0;
		if (rA == null)
			return -1;
		if (rB == null)
			return 1;

		int compareType = ObjectUtils.compare(rA.type, rB.type);
		if (compareType != 0)
			return compareType;

		int compareValue = ObjectUtils.compare(rA.value, rB.value);
		if (compareValue != 0)
			return compareValue;

		return 0;
	}

	private String type;
	private String value;

	ReviewReason(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	ReviewReason() {

	}

	public abstract Long getId();

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
