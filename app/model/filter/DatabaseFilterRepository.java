package model.filter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import db.DbFilter;
import db.DbFilterAction;
import db.DbFilterHistory;
import db.DbFilterType;
import db.DbPlatformAccountGroup;
import util.Utils;

class DatabaseFilterRepository implements FilterRepository {

	DatabaseFilterRepository() {
	}

	@Override
	public List<Filter> findAllFilters() {
		return DatabaseFiltersCache.findAllFilters();
	}

	@Override
	public List<PersonFilter> findAllPersonFilters() {
		return DbFilter.FINDER.fetch(DbFilter.COLUMN_PLATFORM_ACCOUNT_GROUPS).where()
				.eq(DbFilter.COLUMN_TYPE, DbFilterType.PERSON).findList().stream().map(x -> new DatabasePersonFilter(x))
				.collect(Collectors.toList());
	}

	@Override
	public List<EmailFilter> findAllEmailFilters() {
		return DbFilter.FINDER.fetch(DbFilter.COLUMN_PLATFORM_ACCOUNT_GROUPS).where()
				.eq(DbFilter.COLUMN_TYPE, DbFilterType.EMAIL).findList().stream().map(x -> new DatabaseEmailFilter(x))
				.collect(Collectors.toList());
	}

	@Override
	public List<PhoneFilter> findAllPhoneFilters() {
		return DbFilter.FINDER.fetch(DbFilter.COLUMN_PLATFORM_ACCOUNT_GROUPS).where()
				.eq(DbFilter.COLUMN_TYPE, DbFilterType.PHONE).findList().stream().map(x -> new DatabasePhoneFilter(x))
				.collect(Collectors.toList());
	}

	@Override
	public PersonFilter savePersonFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatGenericFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		return (PersonFilter) this.saveGenericFilter(name, country, zip, description, DbFilterType.PERSON,
				platformAccountGroupIds);		
	}

	@Override
	public EmailFilter saveEmailFilter(String name, String description, Long[] platformAccountGroupIds) {

		EmailFilter filter;

		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatEmail(name);

		// create entity and save
		final DbFilter dbFilter = new DbFilter();
		dbFilter.setType(DbFilterType.EMAIL);
		dbFilter.setValue(name);
		dbFilter.setDescription(description);
		dbFilter.setPlatformAccountGroups(getDbPlatformAccountGroups(platformAccountGroupIds));
		dbFilter.save();
		filter = new DatabaseEmailFilter(dbFilter);

		saveFilterHistory(filter, DbFilterType.EMAIL, DbFilterAction.ADD);

		return filter;
	}

	@Override
	public PhoneFilter savePhoneFilter(String name, String description, Long[] platformAccountGroupIds) {

		PhoneFilter filter;

		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatPhone(name);

		// create entity and save
		final DbFilter dbFilter = new DbFilter();
		dbFilter.setType(DbFilterType.PHONE);
		dbFilter.setValue(name);
		dbFilter.setDescription(description);

		dbFilter.setPlatformAccountGroups(getDbPlatformAccountGroups(platformAccountGroupIds));
		dbFilter.save();
		filter = new DatabasePhoneFilter(dbFilter);

		saveFilterHistory(filter, DbFilterType.PHONE, DbFilterAction.ADD);

		return filter;
	}

	@Override
	public List<CompanyFilter> findAllCompanyFilters() {
		return DbFilter.FINDER.fetch(DbFilter.COLUMN_PLATFORM_ACCOUNT_GROUPS).where()
				.eq(DbFilter.COLUMN_TYPE, DbFilterType.COMPANY).findList().stream()
				.map(x -> new DatabaseCompanyFilter(x)).collect(Collectors.toList());
	}

	@Override
	public CompanyFilter saveCompanyFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatCompanyFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		return (CompanyFilter) this.saveGenericFilter(name, country, zip, description, DbFilterType.COMPANY,
				platformAccountGroupIds);
	}

	@Override
	public List<StreetFilter> findAllStreetFilters() {
		return DbFilter.FINDER.fetch(DbFilter.COLUMN_PLATFORM_ACCOUNT_GROUPS).where()
				.eq(DbFilter.COLUMN_TYPE, DbFilterType.STREET).findList().stream().map(x -> new DatabaseStreetFilter(x))
				.collect(Collectors.toList());
	}

	@Override
	public StreetFilter saveStreetFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatStreetFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		return (StreetFilter) this.saveGenericFilter(name, country, zip, description, DbFilterType.STREET,
				platformAccountGroupIds);
	}

	private GenericFilter saveGenericFilter(String name, String country, String zip, String description,
			DbFilterType type,
			Long[] platformAccountGroupIds) {

		GenericFilter filter;

		// create entity and save
		final DbFilter dbFilter = new DbFilter();
		dbFilter.setType(type);
		dbFilter.setValue(name);
		dbFilter.setCountry(country);
		dbFilter.setZip(zip);
		dbFilter.setDescription(description);
		dbFilter.setPlatformAccountGroups(getDbPlatformAccountGroups(platformAccountGroupIds));
		dbFilter.save();
		switch (type) {
		case PERSON:
			filter = new DatabasePersonFilter(dbFilter);
			break;
		case COMPANY:
			filter = new DatabaseCompanyFilter(dbFilter);
			break;
		case STREET:
			filter = new DatabaseStreetFilter(dbFilter);
			break;
		default:
			throw new IllegalArgumentException("type of filter does not exist");
		}

		saveFilterHistory(filter, type, DbFilterAction.ADD);

		return filter;

	}

	@Override
	public PersonFilter updatePersonFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatGenericFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		return (PersonFilter) this.updateGenericFilter(id, name, country, zip, description, DbFilterType.PERSON,
				platformAccountGroupIds);
	}
	@Override
	public EmailFilter updateEmailFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {

		EmailFilter filter;
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatEmail(name);

		// check arguments
		if (id == null)
			throw new IllegalArgumentException("id is required");

		// check if it exists
		DbFilter existed = DbFilter.FINDER.where().eq(DbFilter.COLUMN_ID, id).findUnique();
		if (existed == null)
			throw new IllegalArgumentException("no filter with that id");

		if (!existed.getType().equals(DbFilterType.EMAIL))
			throw new IllegalArgumentException("the type of filter cannot be updated");

		// update Entity
		existed.setValue(name);
		existed.setDescription(description);
		existed.setPlatformAccountGroups(getDbPlatformAccountGroups(platformAccountGroupIds));
		existed.update();

		filter = new DatabaseEmailFilter(existed);

		saveFilterHistory(filter, DbFilterType.EMAIL, DbFilterAction.MODIFY);

		return filter;

	}

	@Override
	public PhoneFilter updatePhoneFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {

		PhoneFilter filter;

		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatPhone(name);

		// check if it exists
		DbFilter existed = DbFilter.FINDER.where().eq(DbFilter.COLUMN_ID, id).findUnique();
		if (existed == null)
			throw new IllegalArgumentException("no filter with that id");

		if (!existed.getType().equals(DbFilterType.PHONE))
			throw new IllegalArgumentException("the type of filter cannot be updated");

		// update Entity
		existed.setValue(name);
		existed.setDescription(description);
		existed.setPlatformAccountGroups(getDbPlatformAccountGroups(platformAccountGroupIds));
		existed.update();

		filter = new DatabasePhoneFilter(existed);

		saveFilterHistory(filter, DbFilterType.PHONE, DbFilterAction.MODIFY);

		return filter;

	}

	@Override
	public CompanyFilter updateCompanyFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatCompanyFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		return (CompanyFilter) this.updateGenericFilter(id, name, country, zip, description, DbFilterType.COMPANY,
				platformAccountGroupIds);
	}
	@Override
	public StreetFilter updateStreetFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatStreetFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		return (StreetFilter) this.updateGenericFilter(id, name, country, zip, description, DbFilterType.STREET,
				platformAccountGroupIds);
	}

	private GenericFilter updateGenericFilter(Long id, String name, String country, String zip, String description,
			DbFilterType type, Long[] platformAccountGroupIds) {

		GenericFilter filter;

		if (id == null)
			throw new IllegalArgumentException("id is required");

		// check if it exists
		DbFilter existed = DbFilter.FINDER.where().eq(DbFilter.COLUMN_ID, id).findUnique();
		if (existed == null)
			throw new IllegalArgumentException("no filter with that id");

		if (!existed.getType().equals(type))
			throw new IllegalArgumentException("the type of filter cannot be updated");

		// update Entity
		existed.setValue(name);
		existed.setCountry(country);
		existed.setZip(zip);
		existed.setDescription(description);
		existed.setPlatformAccountGroups(getDbPlatformAccountGroups(platformAccountGroupIds));
		existed.update();

		switch (type) {
		case PERSON:
			filter = new DatabasePersonFilter(existed);
			break;
		case COMPANY:
			filter = new DatabaseCompanyFilter(existed);
			break;
		case STREET:
			filter = new DatabaseStreetFilter(existed);
			break;
		default:
			throw new IllegalArgumentException("type of filter does not exist");
		}

		saveFilterHistory(filter, type, DbFilterAction.MODIFY);

		return filter;

	}

	@Override
	public void deletePersonFilter(Long id) {
		this.deleteGenericFilter(id, DbFilterType.PERSON);
	}
	@Override
	public void deleteCompanyFilter(Long id) {
		this.deleteGenericFilter(id, DbFilterType.COMPANY);
	}
	@Override
	public void deleteStreetFilter(Long id) {
		this.deleteGenericFilter(id, DbFilterType.STREET);
	}

	@Override
	public void deleteEmailFilter(Long id) {
		this.deleteGenericFilter(id, DbFilterType.EMAIL);
	}

	@Override
	public void deletePhoneFilter(Long id) {
		this.deleteGenericFilter(id, DbFilterType.PHONE);
	}

	private void deleteGenericFilter(Long id, DbFilterType type) {
		// check arguments
		if (id == null)
			throw new IllegalArgumentException("id is required");

		// check if it exists
		DbFilter existed = DbFilter.FINDER.where().eq(DbFilter.COLUMN_ID, id).findUnique();
		if (existed == null)
			throw new IllegalArgumentException("no filter with that id");

		if (!existed.getType().equals(type))
			throw new IllegalArgumentException("cannot delete a filter of other type");

		// delete Entity
		existed.delete();

		saveFilterHistory(existed.getId(), existed.getValue(), type, DbFilterAction.DELETE);

	}

	@Override
	public int getCountOfPersonFilters() {
		return getNumberOfRowsOfFilter(DbFilterType.PERSON);
	}

	@Override
	public int getCountOfCompanyFilters() {
		return getNumberOfRowsOfFilter(DbFilterType.COMPANY);
	}

	@Override
	public int getCountOfStreetFilters() {
		return getNumberOfRowsOfFilter(DbFilterType.STREET);
	}

	@Override
	public int getCountOfEmailFilters() {
		return getNumberOfRowsOfFilter(DbFilterType.EMAIL);
	}

	@Override
	public int getCountOfPhoneFilters() {
		return getNumberOfRowsOfFilter(DbFilterType.PHONE);
	}

	private int getNumberOfRowsOfFilter(DbFilterType type) {
		return DbFilter.FINDER.where().eq(DbFilter.COLUMN_TYPE, type).findRowCount();
	}

	private List<DbPlatformAccountGroup> getDbPlatformAccountGroups(Long[] platformAccountGroupIds) {
		List<DbPlatformAccountGroup> dbPlatformAccountGroups = new ArrayList<>();
		if (platformAccountGroupIds != null) {
			for (Long platformAccountGroupId : platformAccountGroupIds) {
				dbPlatformAccountGroups.add(getDbPlatformAccountGroup(platformAccountGroupId));
			}
		}
		return dbPlatformAccountGroups;
	}

	private DbPlatformAccountGroup getDbPlatformAccountGroup(Long platformAccountGroupId) {
		DbPlatformAccountGroup dbPlatformAccountGroup = null;
		if (platformAccountGroupId != null) {
			dbPlatformAccountGroup = DbPlatformAccountGroup.FINDER.byId(platformAccountGroupId);
			if (dbPlatformAccountGroup == null)
				throw new IllegalArgumentException("Unknown platformAccountGroupId " + platformAccountGroupId);
		}
		return dbPlatformAccountGroup;
	}
	
	private void saveFilterHistory(GenericFilter filter, DbFilterType type, DbFilterAction action) {
		saveFilterHistory(filter.getId(), filter.getName(), type, action);
	}

	private void saveFilterHistory(Long filterId, String name, DbFilterType type, DbFilterAction action) {
		DbFilterHistory history = new DbFilterHistory();
		history.setFilterId(filterId);
		history.setName(name);
		history.setType(type);
		history.setAction(action);
		history.setModified(LocalDateTime.now(Clock.systemUTC()).withNano(0));
		history.setModifiedBy(Utils.getLoginUser());
		history.save();
	}

	@Override
	public List<FilterHistory> retrieveHistory(Long filterId) {
		return DbFilterHistory.FINDER.where().eq(DbFilterHistory.COLUMN_ID, filterId).findList().stream()
				.map(x -> new DatabaseFilterHistory(x)).collect(Collectors.toList());
	}

	public List<Filter> getAllFilters() {
		final List<Filter> result = new ArrayList<>();
		result.addAll(findAllPersonFilters());
		result.addAll(findAllCompanyFilters());
		result.addAll(findAllStreetFilters());
		result.addAll(findAllEmailFilters());
		result.addAll(findAllPhoneFilters());
		return result;
	}

	@Override
	public CompanyFilter findCompanyFilter(Long id) {
		DbFilter dbFilter = DbFilter.FINDER.byId(id);
		return dbFilter != null ? new DatabaseCompanyFilter(dbFilter) : null;
	}

	@Override
	public StreetFilter findStreetFilter(Long id) {
		DbFilter dbFilter = DbFilter.FINDER.byId(id);
		return dbFilter != null ? new DatabaseStreetFilter(dbFilter) : null;
	}

	@Override
	public PhoneFilter findPhoneFilter(Long id) {
		DbFilter dbFilter = DbFilter.FINDER.byId(id);
		return dbFilter != null ? new DatabasePhoneFilter(dbFilter) : null;
	}

	@Override
	public PersonFilter findPersonFilter(Long id) {
		DbFilter dbFilter = DbFilter.FINDER.byId(id);
		return dbFilter != null ? new DatabasePersonFilter(dbFilter) : null;
	}

	@Override
	public EmailFilter findEmailFilter(Long id) {
		DbFilter dbFilter = DbFilter.FINDER.byId(id);
		return dbFilter != null ? new DatabaseEmailFilter(dbFilter) : null;
	}
}
