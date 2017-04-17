package model.testrun;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class TestCase {

	private String name;
	private String expected;
	private String actual;
	private TestResult result;

	TestCase() {
	}

	TestCase(String name, String actual, String expected) {
		this.name = name;
		this.actual = actual;
		this.expected = expected;
		this.result = (this.actual.equals(this.expected)) ? TestResult.PASSED : TestResult.FAILED;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExpected() {
		return expected;
	}
	public void setExpected(String expected) {
		this.expected = expected;
	}
	public String getActual() {
		return actual;
	}
	public void setActual(String actual) {
		this.actual = actual;
	}

	public TestResult getResult() {
		return result;
	}

	public abstract Long getId();

	@Override
	public int hashCode() {
		final HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(name);
		return builder.hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TestCase))
			return false;

		final TestCase other = (TestCase) obj;
		final EqualsBuilder builder = new EqualsBuilder();
		builder.append(name, other.name);

		return builder.isEquals();
	}

	public boolean hasConflicts(TestCase t2) {

		boolean hasSameName = this.getName().equalsIgnoreCase(t2.getName());
		boolean hasSameActual = this.getActual().equalsIgnoreCase(t2.getActual());
		boolean hasSameExpected = this.getExpected().equalsIgnoreCase(t2.getExpected());

		if (hasSameName && !hasSameActual)
			return true;
		if (hasSameName && !hasSameExpected)
			return true;

		return false;

	}
	public boolean isIdentic(Object obj) {
		if (!(obj instanceof TestCase))
			return false;

		final TestCase other = (TestCase) obj;
		final EqualsBuilder builder = new EqualsBuilder();
		builder.append(name, other.name);
		builder.append(expected, other.expected);
		builder.append(actual, other.actual);
		return builder.isEquals();
	}
	public boolean hasEmptyValues() {

		if (StringUtils.isBlank(this.getName()) || StringUtils.isBlank(this.getActual())
				|| StringUtils.isBlank(this.getExpected())) {
			return true;
		}
		return false;
	}

}
