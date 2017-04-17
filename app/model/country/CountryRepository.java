package model.country;

import java.util.List;

public interface CountryRepository {

	List<Country> getCountries();
	boolean countryIsValid(String code);

}
