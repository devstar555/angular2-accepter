package model.zipcode;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import model.country.CountryRepository;
import model.country.CountryRepositoryFactory;

/**
 * <p>
 * Validations and formatting for input
 * </p>
 * <p>
 * The idea of this class is to centralize as much as possible the most common
 * validations and formatting of fields
 * </p>
 */
class Validator {

	private static final CountryRepository countryRepository = CountryRepositoryFactory.get();

	private static final Pattern ZIP_PATTERN = Pattern.compile("^\\d{1,5}$");
	private static final Pattern LATITUDE_PATTERN = Pattern
			.compile("^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$");
	private static final Pattern LONGITUDE_PATTERN = Pattern
			.compile("^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$");

	private Validator() {
	}

	static String validateAndFormatCountry(String country) {

		String newCountry = StringUtils.trimToNull(country);

		if (newCountry == null)
			throw new IllegalArgumentException("country code is required");

		newCountry = StringUtils.trimToNull(newCountry.toUpperCase());
		if (!StringUtils.isBlank(country) && !countryRepository.countryIsValid(newCountry))
			throw new IllegalArgumentException("the country code entered does not exist");

		return newCountry;
	}

	public static String validateAndFormatZip(final String zip) {

		String newZip = StringUtils.trimToNull(zip);

		if (newZip == null)
			throw new IllegalArgumentException("zip code is required");

		if (!ZIP_PATTERN.matcher(newZip).matches()) {
			throw new IllegalArgumentException("zip code may only contain upto 5 digits");
		}

		return newZip;
	}

	public static String validateAndFormatLatitude(String latitude) {

		String newLatitude = StringUtils.trimToNull(latitude);

		if (newLatitude == null)
			throw new IllegalArgumentException("latitude is required");

		if (!LATITUDE_PATTERN.matcher(newLatitude).matches()) {
			throw new IllegalArgumentException("invalid latitude");
		}

		return newLatitude;
	}

	public static String validateAndFormatLongitude(String longitude) {

		String newLongitude = StringUtils.trimToNull(longitude);

		if (newLongitude == null)
			throw new IllegalArgumentException("longitude is required");

		if (!LONGITUDE_PATTERN.matcher(newLongitude).matches()) {
			throw new IllegalArgumentException("invalid longitude");
		}

		return newLongitude;
	}

}
