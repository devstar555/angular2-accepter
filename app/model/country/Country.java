package model.country;

import org.apache.commons.lang3.builder.CompareToBuilder;

public abstract class Country implements Comparable<Country> {

	private String code;
	private String name;

	public abstract Long getId();

	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int compareTo(Country other) {
		return new CompareToBuilder().append(this.getName(), other.getName()).toComparison();
	}

}
