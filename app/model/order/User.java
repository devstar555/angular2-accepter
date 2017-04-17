package model.order;

public class User {

	private final Long externalId;
	private final String firstname;
	private final String lastname;
	private final String username;
	private final String mail;

	User(Long externalId, String firstname, String lastname, String username, String mail) {
		super();
		this.externalId = externalId;
		this.firstname = firstname;
		this.lastname = lastname;
		this.username = username;
		this.mail = mail;
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

	public String getUsername() {
		return username;
	}

	public String getMail() {
		return mail;
	}

}
