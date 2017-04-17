package model.filter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import model.order.Address;
import model.order.Bundle;

public abstract class EmailFilter extends GenericFilter {

	public static final String FILTER_ID = "+emailFilter";

	EmailFilter() {
	}

	EmailFilter(String name, String description, Long[] platformAccountGroupIds) {
		this.name = name;
		this.description = description;
		this.platformAccountGroupIds = platformAccountGroupIds;
	}

	@Override
	protected String getFilterId() {
		return FILTER_ID;
	}

	@Override
	protected List<Match> matchAddress(String word, Address address) {
		return Collections.emptyList();
	}

	@Override
	public List<Match> match(Bundle bundle) {
		final Set<Match> result = new LinkedHashSet<>();

		if (StringUtils.isBlank(bundle.getUser().getMail()))
			return Collections.emptyList();

		if (shouldFilterBeApplied(bundle)) {

			final String normalizedUserMail = StringUtils.trimToNull(bundle.getUser().getMail()).toLowerCase();
			final String normalizedFilter = StringUtils.trimToNull(name).toLowerCase();

			if (matches(normalizedUserMail, normalizedFilter)
					|| matches(replaceUmlautes(normalizedUserMail), replaceUmlautes(normalizedFilter)))
				result.add(match(name, bundle.getUser(), FIELD_MAIL));
		}

		return result.stream().collect(Collectors.toList());
	}

	private boolean matches(String userMail, String filter) {

		// special handling for generated amazon mail addresses
		for (String curMaskedDomain : MASKED_ADDRESS_DOMAINS) {
			if (userMail.contains(curMaskedDomain)) {
				return userMail.startsWith(filter);
			}
		}

		// default handling
		return userMail.contains(filter);

	}

}
