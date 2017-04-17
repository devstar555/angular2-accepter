package model.filter;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import model.order.Address;
import model.order.Bundle;

public abstract class PhoneFilter extends GenericFilter {

	public static final String FILTER_ID = "+phoneFilter";
	private static final Pattern PLUS_PATTERN = Pattern.compile("\\+");

	PhoneFilter() {
	}

	PhoneFilter(String name, String description, Long[] platformAccountGroupIds) {
		this.name = name;
		this.description = description;
		this.platformAccountGroupIds = platformAccountGroupIds;
	}

	@Override
	protected String getFilterId() {
		return FILTER_ID;
	}

	@Override
	public List<Match> match(Bundle bundle) {
		final Set<Match> result = Collections.synchronizedSet(new LinkedHashSet<>());
		if (shouldFilterBeApplied(bundle)) {
			bundle.getAddresses().parallelStream().forEach(cur -> {
				String phoneToCompare = transformFieldValue(cur.getPhone());
				if (StringUtils.contains(phoneToCompare, name))
					result.add(match(name, cur, FIELD_PHONE));
			});
		}
		return result.stream().collect(Collectors.toList());
	}

	private String transformFieldValue(String fieldValue) {
		if (StringUtils.isNotBlank(fieldValue)) {
			return PLUS_PATTERN.matcher(NUMBERS_AND_PLUS_PATTERN.matcher(StringUtils.trimToNull(fieldValue)).replaceAll(""))
					.replaceAll("00");
		}
		return fieldValue;
	}

	// does not apply on this case as all the logic is inside the match
	// method of this class
	@Override
	protected List<Match> matchAddress(String word, Address address) {
		return Collections.emptyList();
	}

}
