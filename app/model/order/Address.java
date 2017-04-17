package model.order;

public class Address {

	private final Long externalId;
	private final String firstname;
	private final String lastname;
	private final String company;
	private final String address1;
	private final String address2;
	private final String address3;
	private final String zip;
	private final String city;
	private final String country;
	private final String phone;

	Address(Long externalId, String firstname, String lastname, String company, String address1, String address2,
			String address3, String zip, String city, String country, String phone) {
		super();
		this.externalId = externalId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.company = company;
		this.address1 = address1;
		this.address2 = address2;
		this.address3 = address3;
		this.zip = zip;
		this.city = city;
		this.country = country;
		this.phone = phone;
	}

	public Long getExternalId() {
		return externalId;
	}

	public String getFirstname() {
		return firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public String getCompany() {
		return company;
	}

	public String getAddress1() {
		return address1;
	}

	public String getAddress2() {
		return address2;
	}

	public String getAddress3() {
		return address3;
	}

	public String getZip() {
		return zip;
	}

	public String getCity() {
		return city;
	}

	public String getCountry() {
		return country;
	}

	public String getPhone() {
		return phone;
	}

}
