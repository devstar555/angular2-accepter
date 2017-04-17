package model.platformaccountgroup;

import org.apache.commons.lang3.StringUtils;

import db.DbPlatformAccountGroup;

class DatabasePlatformAccountGroup extends PlatformAccountGroup {

	private Long id;

	DatabasePlatformAccountGroup(DbPlatformAccountGroup platformGroup) {
		super(platformGroup.getName(), platformGroup.getDescription(),
				StringUtils.split(platformGroup.getAccounts(), ","));
		this.id = platformGroup.getId();
	}

	public DatabasePlatformAccountGroup() {
		super();
	}

	@Override
	public Long getId() {
		return id;
	}

}
