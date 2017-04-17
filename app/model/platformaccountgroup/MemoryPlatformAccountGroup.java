package model.platformaccountgroup;

class MemoryPlatformAccountGroup extends PlatformAccountGroup {

	private Long id;

	MemoryPlatformAccountGroup(Long id, String name, String description, String[] accounts) {
		super(name, description, accounts);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
