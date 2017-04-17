package model.filter;

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

	static String validateAndFormatEmail(String name) {

		name = StringUtils.trimToNull(name);

		if (name == null)
			throw new IllegalArgumentException("the email is required");

		if (!validateFilterEmail(name))
			throw new IllegalArgumentException("the email is not valid");

		return name;
	}

	static String validateAndFormatPhone(String name) {

		// prepare arguments
		name = StringUtils.trimToNull(name);
		if (name == null)
			throw new IllegalArgumentException("phone is required");

		// delete every character different from numbers and the plus sign
		name = name.replaceAll("[^+0-9]", "");

		// check plus sign
		int plusSymbols = StringUtils.countMatches(name, "+");
		if (plusSymbols > 1)
			throw new IllegalArgumentException("cannot contain more than one plus sign");
		if (plusSymbols == 1 && !name.startsWith("+"))
			throw new IllegalArgumentException("cannot have plus sign in the middle of the number");

		name = name.replaceAll("\\+", "00");

		if (name.length() < 5)
			throw new IllegalArgumentException("the phone filter entered is too short (min size 5)");

		return name;
	}

	static String formatDescription(String description) {
		if (description != null)
			description = StringUtils.trimToNull(description);

		return description;
	}

	static String validateAndFormatStreetFilterName(String name) {

		name = StringUtils.trimToNull(name);
		if (name == null)
			throw new IllegalArgumentException("the name is required");

		name = name.toLowerCase().replaceAll("[\\s]+", " ");
		if (name.matches(".*[^\\p{IsAlphabetic}\\p{IsDigit}\\s].*"))
			throw new IllegalArgumentException("name may only contain alphabetic, numeric and whitespace characters");

		return name;
	}

	static String validateAndFormatGenericFilterName(String name) {

		name = StringUtils.trimToNull(name);
		if (name == null)
			throw new IllegalArgumentException("the name is required");

		name = name.toLowerCase().replaceAll("[\\s]+", " ");
		if (name.matches(".*[^\\p{IsAlphabetic}\\s].*"))
			throw new IllegalArgumentException("name may only contain alphabetic and whitespace characters");

		return name;
	}

	static String validateAndFormatGenericFilterCountry(String country) {

		if (country != null) {
			country = StringUtils.trimToNull(country.toUpperCase());
			if (!StringUtils.isBlank(country) && !countryRepository.countryIsValid(country))
				throw new IllegalArgumentException("the country code entered does not exist");
		}

		return country;
	}

	// this method is not a formal validator of a correct email
	// it just makes a couple of checks for the email filters of this app
	// it was added as the app expects as valid filters those starting with the
	// @ character
	private static boolean validateFilterEmail(String emailStr) {
		boolean correctAt = StringUtils.countMatches(emailStr, "@") == 1;
		boolean containsDotAfterAt = StringUtils.countMatches(StringUtils.substringAfter(emailStr, "@"), ".") >= 1;
		return correctAt && containsDotAfterAt;
	}

	public static String validateAndFormatCompanyFilterName(String name) {

		name = StringUtils.trimToNull(name);
		if (name == null)
			throw new IllegalArgumentException("the name is required");

		name = name.toLowerCase().replaceAll("[\\s]+", " ");
		if (name.matches(".*[^\\p{IsAlphabetic}\\p{IsDigit}\\s].*"))
			throw new IllegalArgumentException("name may only contain alphabetic, numeric and whitespace characters");

		return name;
	}

	public static String validateAndFormatGenericFilterZip(final String countryCode, final String zip) {

		String newZip = StringUtils.trimToNull(zip);

		if (newZip != null) {
			if (StringUtils.isBlank(countryCode)) {
				throw new IllegalArgumentException("zip code without country not allowed");
			}

			if (!ZIP_PATTERN.matcher(newZip).matches()) {
				throw new IllegalArgumentException("zip code may only contain upto 5 digits");
			}
		}

		return newZip;
	}

}

