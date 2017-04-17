package model.filter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Match {
	private final String type;
	private final String value;
	private final String message;

	Match(String type, String value, String message) {
		this.type = type;
		this.value = value;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public String getType() {
		return type;
	}

	public String getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Match))
			return false;

		final Match other = (Match) obj;
		final EqualsBuilder builder = new EqualsBuilder();
		builder.append(other.getMessage(), this.getMessage());
		builder.append(other.getValue(), this.getValue());
		builder.append(other.getType(), this.getType());
		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(this.getMessage());
		builder.append(this.getValue());
		builder.append(this.getType());
		return builder.toHashCode();
	}
}
