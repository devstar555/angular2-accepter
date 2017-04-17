package model.filter;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import db.DbFilterAction;
import db.DbFilterType;
import util.Utils;

class MemoryFilterRepository implements FilterRepository {

	private final AtomicLong personFilterId = new AtomicLong(1);
	private final AtomicLong companyFilterId = new AtomicLong(1);
	private final AtomicLong streetFilterId = new AtomicLong(1);
	private final AtomicLong emailFilterId = new AtomicLong(1);
	private final AtomicLong phoneFilterId = new AtomicLong(1);
	private final AtomicLong filterHistoryId = new AtomicLong(1);
	private final List<PersonFilter> personFilters = new ArrayList<>();
	private final List<CompanyFilter> companyFilters = new ArrayList<>();
	private final List<StreetFilter> streetFilters = new ArrayList<>();
	private final List<EmailFilter> emailFilters = new ArrayList<>();
	private final List<PhoneFilter> phoneFilters = new ArrayList<>();
	private final List<FilterHistory> filterHistories = new ArrayList<>();

	MemoryFilterRepository() {
	}

	@Override
	public List<Filter> findAllFilters() {
		final List<Filter> result = new ArrayList<>();
		result.addAll(personFilters);
		result.addAll(companyFilters);
		result.addAll(streetFilters);
		result.addAll(emailFilters);
		result.addAll(phoneFilters);
		return result;
	}

	@Override
	public List<PersonFilter> findAllPersonFilters() {
		return new ArrayList<>(personFilters);
	}

	@Override
	public List<EmailFilter> findAllEmailFilters() {
		return new ArrayList<>(emailFilters);
	}

	@Override
	public List<PhoneFilter> findAllPhoneFilters() {
		return new ArrayList<>(phoneFilters);
	}

	@Override
	public PersonFilter savePersonFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {

		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatGenericFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		// create entity and store
		final PersonFilter result = new MemoryPersonFilter(personFilterId.getAndIncrement(), name, country, zip,
				description,
				platformAccountGroupIds);
		saveFilterLogger(result, DbFilterType.PERSON, DbFilterAction.ADD);
		personFilters.add(result);
		return result;

	}

	@Override
	public EmailFilter saveEmailFilter(String name, String description, Long[] platformAccountGroupIds) {

		// prepare arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatEmail(name);

		// create object and store
		final EmailFilter result = new MemoryEmailFilter(emailFilterId.getAndIncrement(), name, description,
				platformAccountGroupIds);
		emailFilters.add(result);
		saveFilterLogger(result, DbFilterType.EMAIL, DbFilterAction.ADD);
		return result;

	}

	@Override
	public PhoneFilter savePhoneFilter(String name, String description, Long[] platformAccountGroupIds) {

		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatPhone(name);

		// create entity and store
		final PhoneFilter result = new MemoryPhoneFilter(phoneFilterId.getAndIncrement(), name, description,
				platformAccountGroupIds);
		phoneFilters.add(result);
		saveFilterLogger(result, DbFilterType.PHONE, DbFilterAction.ADD);
		return result;

	}

	@Override
	public List<CompanyFilter> findAllCompanyFilters() {
		return new ArrayList<>(companyFilters);
	}

	@Override
	public CompanyFilter saveCompanyFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {

		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatCompanyFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		// create entity and store
		final CompanyFilter result = new MemoryCompanyFilter(companyFilterId.getAndIncrement(), name, country, zip,
				description, platformAccountGroupIds);
		companyFilters.add(result);
		saveFilterLogger(result, DbFilterType.COMPANY, DbFilterAction.ADD);
		return result;

	}

	@Override
	public List<StreetFilter> findAllStreetFilters() {
		return new ArrayList<>(streetFilters);
	}

	@Override
	public StreetFilter saveStreetFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {

		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatStreetFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		// create entity and store
		final StreetFilter result = new MemoryStreetFilter(streetFilterId.getAndIncrement(), name, country, zip,
				description,
				platformAccountGroupIds);
		streetFilters.add(result);
		saveFilterLogger(result, DbFilterType.STREET, DbFilterAction.ADD);
		return result;

	}

	@Override
	public PersonFilter updatePersonFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {

		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatGenericFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		if (id == null)
			throw new IllegalArgumentException("id is required");

		PersonFilter result = personFilters.stream().filter(filter -> filter.getId() == id).findAny().orElse(null);
		if (result == null)
			throw new IllegalArgumentException("no filter exists for that id");
		else {
			result.setName(name);
			result.setDescription(description);
			result.setCountry(country);
			result.setZip(zip);
			result.setPlatformAccountGroupIds(platformAccountGroupIds);
		}
		saveFilterLogger(result, DbFilterType.PERSON, DbFilterAction.MODIFY);
		return result;
	}

	@Override
	public EmailFilter updateEmailFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {

		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatEmail(name);

		// check arguments
		if (id == null)
			throw new IllegalArgumentException("id is required");

		EmailFilter result = emailFilters.stream().filter(filter -> filter.getId() == id).findAny().orElse(null);
		if (result == null)
			throw new IllegalArgumentException("no filter exists for that id");
		else {
			result.setName(name);
			result.setDescription(description);
			result.setPlatformAccountGroupIds(platformAccountGroupIds);
		}
		saveFilterLogger(result, DbFilterType.EMAIL, DbFilterAction.MODIFY);
		return result;
	}

	@Override
	public PhoneFilter updatePhoneFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {

		// check and format arguments
		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatPhone(name);

		if (id == null)
			throw new IllegalArgumentException("id is required");

		PhoneFilter result = phoneFilters.stream().filter(filter -> filter.getId() == id).findAny().orElse(null);
		if (result == null)
			throw new IllegalArgumentException("no filter exists for that id");
		else {
			result.setName(name);
			result.setDescription(description);
			result.setPlatformAccountGroupIds(platformAccountGroupIds);
		}
		saveFilterLogger(result, DbFilterType.PHONE, DbFilterAction.MODIFY);
		return result;
	}

	@Override
	public CompanyFilter updateCompanyFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {

		// check and format arguments
		if (id == null)
			throw new IllegalArgumentException("id is required");

		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatCompanyFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		CompanyFilter result = companyFilters.stream().filter(filter -> filter.getId() == id).findAny().orElse(null);
		if (result == null)
			throw new IllegalArgumentException("no filter exists for that id");
		else {
			result.setName(name);
			result.setDescription(description);
			result.setCountry(country);
			result.setZip(zip);
			result.setPlatformAccountGroupIds(platformAccountGroupIds);
		}
		saveFilterLogger(result, DbFilterType.COMPANY, DbFilterAction.MODIFY);
		return result;
	}

	@Override
	public StreetFilter updateStreetFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {

		// check and format arguments
		if (id == null)
			throw new IllegalArgumentException("id is required");

		description = Validator.formatDescription(description);
		name = Validator.validateAndFormatStreetFilterName(name);
		country = Validator.validateAndFormatGenericFilterCountry(country);
		zip = Validator.validateAndFormatGenericFilterZip(country, zip);

		StreetFilter result = streetFilters.stream().filter(filter -> filter.getId() == id).findAny().orElse(null);
		if (result == null)
			throw new IllegalArgumentException("no filter exists for that id");
		else {
			result.setName(name);
			result.setDescription(description);
			result.setCountry(country);
			result.setZip(zip);
			result.setPlatformAccountGroupIds(platformAccountGroupIds);
		}
		saveFilterLogger(result, DbFilterType.STREET, DbFilterAction.MODIFY);
		return result;
	}

	@Override
	public void deletePersonFilter(Long id) {

		GenericFilter filter = personFilters.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);

		if (filter == null)
			throw new IllegalArgumentException("no filter exists for that id");

		personFilters.remove(filter);
		saveFilterLogger(filter,
				DbFilterType.PERSON, DbFilterAction.DELETE);
	}

	@Override
	public void deleteEmailFilter(Long id) {
		GenericFilter filter = emailFilters.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
		if (filter == null)
			throw new IllegalArgumentException("no filter exists for that id");

		emailFilters.remove(filter);
		saveFilterLogger(filter, DbFilterType.EMAIL, DbFilterAction.DELETE);
	}

	@Override
	public void deletePhoneFilter(Long id) {
		GenericFilter filter = phoneFilters.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
		if (filter == null)
			throw new IllegalArgumentException("no filter exists for that id");

		phoneFilters.remove(filter);
		saveFilterLogger(filter, DbFilterType.PHONE, DbFilterAction.DELETE);
	}

	@Override
	public void deleteCompanyFilter(Long id) {
		GenericFilter filter = companyFilters.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
		if (filter == null)
			throw new IllegalArgumentException("no filter exists for that id");

		companyFilters.remove(filter);
		saveFilterLogger(filter, DbFilterType.COMPANY, DbFilterAction.DELETE);
	}

	@Override
	public void deleteStreetFilter(Long id) {
		GenericFilter filter = streetFilters.stream().filter(f -> f.getId().equals(id)).findFirst().orElse(null);
		if (filter == null)
			throw new IllegalArgumentException("no filter exists for that id");

		streetFilters.remove(filter);
		saveFilterLogger(filter, DbFilterType.STREET, DbFilterAction.DELETE);
	}

	@Override
	public int getCountOfPersonFilters() {
		return personFilters.size();
	}

	@Override
	public int getCountOfCompanyFilters() {
		return companyFilters.size();
	}

	@Override
	public int getCountOfStreetFilters() {
		return streetFilters.size();
	}

	@Override
	public int getCountOfEmailFilters() {
		return emailFilters.size();
	}

	@Override
	public int getCountOfPhoneFilters() {
		return phoneFilters.size();
	}

	private void saveFilterLogger(GenericFilter filter, DbFilterType type, DbFilterAction action) {
		FilterHistory filterHistory = new MemoryFilterHistory(filterHistoryId.getAndIncrement(), filter.getName(),
				action.toString(), filter.getId(),
				LocalDateTime.now(Clock.systemUTC()).withNano(0), type.toString(), Utils.getLoginUser());
		filterHistories.add(filterHistory);
	}
	@Override
	public List<FilterHistory> retrieveHistory(Long filterId) {
		return new ArrayList<>(filterHistories);
	}

	@Override
	public CompanyFilter findCompanyFilter(Long id) {
		return companyFilters.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public StreetFilter findStreetFilter(Long id) {
		return streetFilters.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public PhoneFilter findPhoneFilter(Long id) {
		return phoneFilters.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public PersonFilter findPersonFilter(Long id) {
		return personFilters.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public EmailFilter findEmailFilter(Long id) {
		return emailFilters.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}
}
