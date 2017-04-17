package model.platformaccountgroup;

public abstract class PlatformAccountGroup {

	private String name;
	private String description;
	private String[] accounts;

	public PlatformAccountGroup(String name, String description, String[] accounts) {
		this.name = name;
		this.description = description;
		this.accounts = accounts;
	}

	public PlatformAccountGroup() {
	}

	public abstract Long getId();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String[] getAccounts() {
		return accounts;
	}

	public void setAccounts(String[] accounts) {
		this.accounts = accounts;
	}

}
