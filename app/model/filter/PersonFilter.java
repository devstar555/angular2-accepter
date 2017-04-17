package model.filter;

import java.util.ArrayList;
import java.util.List;

import model.order.Address;

/**
 * <p>
 * A filter that matches based on a person.
 * </p>
 * <p>
 * A {@link PersonFilter} takes a name and a country as an argument. The name
 * argument can have multiple words. A filter with a name that consists of
 * multiple words matches if (and only if) separate filters with each one of the
 * words would all match (and not only a part of them). The filter matches if
 * the name is contained within the firstname, lastname, company name, username
 * or mail address of either the user or one of the addresses. An address is
 * only checked for matches if it's country equals the specified country. The
 * address is also checked for matches if no country is specified or the address
 * itself does not have a country. If the countries of all the addresses equal
 * the specified country, then the user is not checked for matches, otherwise
 * the user is always checked for matches. A word is only considered to be
 * contained in one of the fields if it is encountered on it's own and not as a
 * substring of another word. A word is considered a sequence of alphabetic
 * characters without non alphabetic characters in between.
 * </p>
 */
public abstract class PersonFilter extends GenericFilter {
	public static final String FILTER_ID = "+personFilter";

	PersonFilter() {

	}

	PersonFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		super(name, country, zip, description, platformAccountGroupIds);
	}

	// methods
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

		return result;
	}

}
