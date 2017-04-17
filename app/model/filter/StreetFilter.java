package model.filter;

import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import model.order.Address;
import model.order.Bundle;

/**
 * <p>
 * A filter that matches based on a street.
 * </p>
 * <p>
 * A {@link StreetFilter} takes a name and a country as an argument. The name
 * argument can have multiple words. A filter with a name that consists of
 * multiple words matches if (and only if) separate filters with each one of the
 * words would all match (and not only a part of them). The filter matches if
 * the name is contained within the address1, address2 or address3 of one of the
 * addresses. An address is only checked for matches if it's country equals the
 * specified country. The address is also checked for matches if no country is
 * specified or the address itself does not have a country. A word is only
 * considered to be contained in one of the fields if it is encountered on it's
 * own and not as a substring of another word. A word is considered a sequence
 * of alphabetic characters without non alphabetic characters in between.
 * </p>
 */

public abstract class StreetFilter extends GenericFilter {

	public static final String FILTER_ID = "+streetFilter";
	private static final Pattern ALPHABETIC_AND_DIGIT_PARENTHESIS_PATTERN = Pattern
			.compile("([\\p{IsAlphabetic}])([\\p{IsDigit}])");
	private static final Pattern DIGIT_AND_ALPHABETIC_PARENTHESIS_PATTERN = Pattern
			.compile("([\\p{IsDigit}])([\\p{IsAlphabetic}])");
	private static final String TERM = "($|[^\\p{IsAlphabetic}\\p{IsDigit}])";
	private static final Pattern PATTERN_STRASSE1 = Pattern.compile(Pattern.quote("straße") + TERM);
	private static final Pattern PATTERN_STR = Pattern.compile(Pattern.quote("str") + TERM);
	private static final Pattern PATTERN_STR_DOT = Pattern.compile(Pattern.quote("str.") + TERM);
	private static final Pattern PATTERN_STRASSE2 = Pattern.compile(Pattern.quote("strasse") + TERM);
	private static final Pattern PATTERN_STREET = Pattern.compile(Pattern.quote("street") + TERM);
	private static final Pattern PATTERN_AVENUE = Pattern.compile(Pattern.quote("avenue") + TERM);
	private static final Pattern PATTERN_LANE = Pattern.compile(Pattern.quote("lane") + TERM);
	private static final Pattern PATTERN_ROAD = Pattern.compile(Pattern.quote("road") + TERM);

	StreetFilter() {
	}

	StreetFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		super(name, country, zip, description, platformAccountGroupIds);
	}

	@Override
	protected String getFilterId() {
		return FILTER_ID;
	}

	@Override
	protected Set<String> extractWordsFromName(String name) {

		String newName = SPACE_PATTERN
				.matcher(ALPHABETIC_AND_DIGIT_PATTERN.matcher(normalizeString(name)).replaceAll(SPACE))
				.replaceAll(SPACE)
				.trim();

		return new HashSet<>(Arrays.asList(SINGLE_SPACE_PATTERN.split(newName)));
	}

	@Override
	protected List<Match> matchBundle(String word, Bundle bundle) {
		return matchAddresses(word, bundle.getAddresses());
	}

	@Override
	protected List<Match> matchAddress(String word, Address address) {
		final List<Match> result = new ArrayList<>();

		if (wordMatches(word, address.getAddress1()))
			result.add(match(word, address, FIELD_ADDRESS_1));
		if (wordMatches(word, address.getAddress2()))
			result.add(match(word, address, FIELD_ADDRESS_2));
		if (wordMatches(word, address.getAddress3()))
			result.add(match(word, address, FIELD_ADDRESS_3));

		return result;
	}

	@Override
	protected boolean doesWordMatch(String word, String fieldValue) {
		return SPACE_PATTERN.matcher(ALPHABETIC_AND_DIGIT_PATTERN
				.matcher(SPACE + normalizeString(fieldValue).toLowerCase() + SPACE).replaceAll(SPACE)).replaceAll(SPACE)
				.contains(SPACE + word.toLowerCase() + SPACE);
	}

	private static final String normalizeString(String value) {
		String newValue = DIGIT_AND_ALPHABETIC_PARENTHESIS_PATTERN
				.matcher(ALPHABETIC_AND_DIGIT_PARENTHESIS_PATTERN.matcher(value.toLowerCase()).replaceAll("$1 $2"))
				.replaceAll("$1 $2");

		// normalize common abbreviations
		return PATTERN_ROAD.matcher(PATTERN_LANE.matcher(PATTERN_AVENUE.matcher(PATTERN_STREET.matcher(PATTERN_STRASSE2
				.matcher(PATTERN_STR_DOT.matcher(
						PATTERN_STR.matcher(PATTERN_STRASSE1.matcher(newValue).replaceAll("st ")).replaceAll("st "))
						.replaceAll("st "))
				.replaceAll("st ")).replaceAll("st ")).replaceAll("ave ")).replaceAll("ln ")).replaceAll("rd ");
	}

}
