package model.filter;

import static org.apache.commons.lang3.StringUtils.SPACE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import model.order.Address;

/**
 * <p>
 * A filter that matches based on a company.
 * </p>
 * <p>
 * A {@link CompanyFilter} takes a name and a country as an argument. The name
 * argument can have multiple words. A filter with a name that consists of
 * multiple words matches if (and only if) separate filters with each one of the
 * words would all match (and not only a part of them). The filter matches if
 * the name is contained within the firstname, lastname, company name, username
 * , mail address, address1, address2 or address3 of either the user or one of
 * the addresses. An address is only checked for matches if it's country equals
 * the specified country. The address is also checked for matches if no country
 * is specified or the address itself does not have a country. If the countries
 * of all the addresses equal the specified country, then the user is not
 * checked for matches, otherwise the user is always checked for matches. A word
 * is only considered to be contained in one of the fields if it is encountered
 * on it's own and not as a substring of another word. A word is considered a
 * sequence of alphabetic characters without non alphabetic characters
 * inbetween.
 * </p>
 */
public abstract class CompanyFilter extends GenericFilter {

	public static final String FILTER_ID = "+companyFilter";

	CompanyFilter() {
	}

	CompanyFilter(String name, String country, String zipCode, String description,
			Long[] platformAccountGroupIds) {
		super(name, country, zipCode, description, platformAccountGroupIds);
	}

	@Override
	protected String getFilterId() {
		return FILTER_ID;
	}

	@Override
	protected List<Match> matchAddress(String word, Address address) {
		final List<Match> result = new ArrayList<>();

		if (wordMatches(word, address.getFirstname()))
			result.add(match(word, address, FIELD_FIRSTNAME));
		if (wordMatches(word, address.getLastname()))
			result.add(match(word, address, FIELD_LASTNAME));
		if (wordMatches(word, address.getCompany()))
			result.add(match(word, address, FIELD_COMPANY));
		if (wordMatches(word, address.getAddress1()))
			result.add(match(word, address, FIELD_ADDRESS_1));
		if (wordMatches(word, address.getAddress2()))
			result.add(match(word, address, FIELD_ADDRESS_2));
		if (wordMatches(word, address.getAddress3()))
			result.add(match(word, address, FIELD_ADDRESS_3));

		return result;
	}

	@Override
	protected Set<String> extractWordsFromName(String name) {
		String newName = SPACE_PATTERN.matcher(ALPHABETIC_AND_DIGIT_PATTERN.matcher(name).replaceAll(SPACE))
				.replaceAll(SPACE).trim();
		return new HashSet<>(Arrays.asList(SINGLE_SPACE_PATTERN.split(newName)));
	}

	@Override
	protected boolean doesWordMatch(String word, String fieldValue) {
		return doesWordMatch(word, fieldValue, ALPHABETIC_PATTERN)
				|| doesWordMatch(word, fieldValue, ALPHABETIC_AND_DIGIT_PATTERN);
	}

	private boolean doesWordMatch(String word, String fieldValue, Pattern removePattern) {
		return SPACE_PATTERN
				.matcher(
						removePattern
								.matcher(
										SPACE + fieldValue.toLowerCase() + SPACE)
								.replaceAll(SPACE))
				.replaceAll(SPACE).contains(SPACE + word.toLowerCase() + SPACE);
	}

}
