package model.config;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Property implements Comparable<Property> {

	private final String name;
	private final String value;
	private final String description;

	Property(DefinedProperty definedProperty) {
		this.name = definedProperty.getName();
		this.value = definedProperty.getDefaultValue();
		this.description = definedProperty.getDescription();
	}

	Property(String name, String value, String description) {
		this.name = name;
		this.value = value;
		this.description = description;
	}

	Property withValue(String value) {
		return new Property(this.name, value, this.description);
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Property))
			return false;

		final Property other = (Property) obj;
		final EqualsBuilder builder = new EqualsBuilder();
		builder.append(name, other.name);

		return builder.isEquals();
	}

	@Override
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(name);
		return builder.toHashCode();
	}

	public int compareTo(Property other) {
		return new CompareToBuilder().append(this.getName(), other.getName()).toComparison();
	}

}
