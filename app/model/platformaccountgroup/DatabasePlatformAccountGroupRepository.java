package model.platformaccountgroup;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import db.DbPlatformAccountGroup;
import util.Utils;

class DatabasePlatformAccountGroupRepository implements PlatformAccountGroupRepository {

	DatabasePlatformAccountGroupRepository() {
	}

	@Override
	public List<PlatformAccountGroup> findAllPlatformAccountGroups() {
		return Utils.mapList(DbPlatformAccountGroup.FINDER.all(), x -> new DatabasePlatformAccountGroup(x));
	}

	@Override
	public void deletePlatformAccountGroup(Long id) {

		// check arguments
		if (id == null)
			throw new IllegalArgumentException("id is required");

		// check if it exists
		DbPlatformAccountGroup existed = DbPlatformAccountGroup.FINDER.where().eq(DbPlatformAccountGroup.COLUMN_ID, id)
				.findUnique();
		if (existed == null)
			throw new IllegalArgumentException("no PlatformAccountGroup with that id");

		existed.delete();
	}

	@Override
	public PlatformAccountGroup savePlatformAccountGroup(String name, String description, String[] accounts) {

		String validatedName = Validator.validateAndFormatName(name);
		String formattedDescription = Validator.formatDescription(description);
		String[] validatedAccounts = Validator.validateAndFormatAccounts(accounts);

		DbPlatformAccountGroup dbGroup = DbPlatformAccountGroup.FINDER.where()
				.eq(DbPlatformAccountGroup.COLUMN_NAME, validatedName).findUnique();
		if (dbGroup != null) {
			throw new IllegalArgumentException("name already exists " + name);
		}
		dbGroup = new DbPlatformAccountGroup();
		dbGroup.setName(validatedName);
		dbGroup.setDescription(formattedDescription);
		dbGroup.setAccounts(StringUtils.join(validatedAccounts, ","));
		dbGroup.save();

		return new DatabasePlatformAccountGroup(dbGroup);
	}

	@Override
	public PlatformAccountGroup updatePlatformGroup(Long id, String name, String description, String[] accounts) {

		String validatedName = Validator.validateAndFormatName(name);
		String formattedDescription = Validator.formatDescription(description);
		String[] validatedAccounts = Validator.validateAndFormatAccounts(accounts);

		if (id == null)
			throw new IllegalArgumentException("id is required");

		// check if it exists
		DbPlatformAccountGroup existed = DbPlatformAccountGroup.FINDER.where().eq(DbPlatformAccountGroup.COLUMN_ID, id)
				.findUnique();
		if (existed == null)
			throw new IllegalArgumentException("no platform account group with that id");

		existed.setName(validatedName);
		existed.setDescription(formattedDescription);
		existed.setAccounts(StringUtils.join(validatedAccounts, ","));
		existed.update();

		return new DatabasePlatformAccountGroup(existed);
	}

	@Override
	public PlatformAccountGroup findById(Long id) {
		return DatabasePlatformAccountGroupCache.findById(id);
	}

	@Override
	public PlatformAccountGroup findPlatformAccountGroup(Long id) {
		DbPlatformAccountGroup group = DbPlatformAccountGroup.FINDER.byId(id);
		return group != null ? new DatabasePlatformAccountGroup(group) : null;
	}
}
