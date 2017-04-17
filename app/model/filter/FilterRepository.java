package model.filter;

import java.util.List;

public interface FilterRepository {
	List<Filter> findAllFilters();

	PersonFilter savePersonFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds);

	PersonFilter updatePersonFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds);
	void deletePersonFilter(Long id);
	List<PersonFilter> findAllPersonFilters();
	int getCountOfPersonFilters();
	List<CompanyFilter> findAllCompanyFilters();

	CompanyFilter updateCompanyFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds);
	void deleteCompanyFilter(Long id);

	CompanyFilter saveCompanyFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds);
	int getCountOfCompanyFilters();
	List<StreetFilter> findAllStreetFilters();

	StreetFilter saveStreetFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds);

	StreetFilter updateStreetFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds);
	void deleteStreetFilter(Long id);
	int getCountOfStreetFilters();
	List<EmailFilter> findAllEmailFilters();

	EmailFilter saveEmailFilter(String name, String description, Long[] platformAccountGroupIds);

	EmailFilter updateEmailFilter(Long id, String name, String description, Long[] platformAccountGroupIds);
	void deleteEmailFilter(Long id);
	int getCountOfEmailFilters();
	List<PhoneFilter> findAllPhoneFilters();

	PhoneFilter savePhoneFilter(String name, String description, Long[] platformAccountGroupIds);

	PhoneFilter updatePhoneFilter(Long id, String name, String description, Long[] platformAccountGroupIds);
	void deletePhoneFilter(Long id);
	int getCountOfPhoneFilters();

	List<FilterHistory> retrieveHistory(Long filterId);

	CompanyFilter findCompanyFilter(Long id);

	StreetFilter findStreetFilter(Long id);

	PhoneFilter findPhoneFilter(Long id);

	PersonFilter findPersonFilter(Long id);

	EmailFilter findEmailFilter(Long id);

}
