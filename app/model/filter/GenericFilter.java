package model.filter;

import static org.apache.commons.lang3.StringUtils.SPACE;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import model.config.Config;
import model.order.Address;
import model.order.Bundle;
import model.order.User;
import model.platformaccountgroup.PlatformAccountGroup;
import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.zipcode.ZipCode;
import model.zipcode.ZipCodeRepositoryFactory;
import util.Utils;

abstract class GenericFilter implements Filter {

	static final String[] MASKED_ADDRESS_DOMAINS = { "@marketplace.amazon.", "@m.marketplace.amazon." };

	protected static final String MESSAGE = "{0} ({1}): {2}({3}).{4} matches {5}";
	protected static final String ENTITY_USER = "User";
	protected static final String ENTITY_ADDRESS = "Address";
	protected static final String FIELD_FIRSTNAME = "firstname";
	protected static final String FIELD_LASTNAME = "lastname";
	protected static final String FIELD_USERNAME = "username";
	protected static final String FIELD_COMPANY = "company";
	protected static final String FIELD_ADDRESS_1 = "address1";
	protected static final String FIELD_ADDRESS_2 = "address2";
	protected static final String FIELD_ADDRESS_3 = "address3";
	protected static final String FIELD_MAIL = "mail";
	protected static final String FIELD_PHONE = "phone";
	protected static final String FIELD_PLATFORM_ACCOUNT_ID = "platformAccountId";

	protected String name;
	protected String country;
	protected String zip;
	protected String description;
	protected Long[] platformAccountGroupIds;

	protected static final Pattern ALPHABETIC_PATTERN = Pattern.compile("[^\\p{IsAlphabetic}]");
	protected static final Pattern ALPHABETIC_AND_DIGIT_PATTERN = Pattern.compile("[^\\p{IsAlphabetic}\\p{IsDigit}]");
	protected static final Pattern DIGIT_AND_ALPHABETIC_PATTERN = Pattern
			.compile("([\\p{IsDigit}])([\\p{IsAlphabetic}])");
	protected static final Pattern SPACE_PATTERN = Pattern.compile("[ ]+");
	protected static final Pattern SINGLE_SPACE_PATTERN = Pattern.compile("[ ]");
	protected static final Pattern NUMBERS_AND_PLUS_PATTERN = Pattern.compile("[^+0-9]");
	protected static final Pattern NUMBERS_PATTERN = Pattern.compile("[^0-9]");
	protected static final Pattern UMLAUT_PATTERN = Pattern.compile("[ßäüö]");
	private static final Object[][] UMLAUT_PATTERN_REPLACEMENTS = {
		{ Pattern.compile("[ß]"), "ss" },
		{ Pattern.compile("[ä]"), "ae" },
		{ Pattern.compile("[ü]"), "ue" },
		{ Pattern.compile("[ö]"), "oe" } };

	GenericFilter() {

	}

	GenericFilter(String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		this.name = name;
		this.country = country;
		this.zip = zip;
		this.description = description;
		this.platformAccountGroupIds = platformAccountGroupIds;
	}

	public abstract Long getId();

	@Override
	public List<Match> match(Bundle bundle) {
		if (!shouldFilterBeApplied(bundle))
			return Collections.emptyList();

		Set<String> words = extractWordsFromName(name);

		final Set<Match> result = Collections.synchronizedSet(new LinkedHashSet<>());
		bundle.isolateAddresses().parallelStream().forEach(isolatedAddress -> {
			if (anyCountryAndDistanceMatch(isolatedAddress)) {
				result.addAll(matchIsolatedAddress(isolatedAddress, words));
			}
		});

		return result.stream().collect(Collectors.toList());
	}

	protected List<Match> matchIsolatedAddress(Bundle bundle, Set<String> words) {
		final List<Match> result = new ArrayList<>();
		for (String word : words) {
			List<Match> wordMatches = matchBundle(word, bundle);
			String newWord = replaceUmlautes(word);
			if (!StringUtils.equals(newWord, word)) {
				wordMatches.addAll(matchBundle(newWord, bundle));
			}
			if (wordMatches.isEmpty())
				return wordMatches;

			result.addAll(wordMatches);
		}

		return result;
	}

	protected boolean shouldFilterBeApplied(Bundle bundle) {
		boolean shouldConsider = true;
		if (bundle.getPlatformAccountId() != null && ArrayUtils.isNotEmpty(platformAccountGroupIds)) {
			for (Long platformAccountGroupId : platformAccountGroupIds) {
				PlatformAccountGroup platformAccountGroup = PlatformAccountGroupRepositoryFactory.get()
						.findById(platformAccountGroupId);
				for (String platformAccountId : platformAccountGroup.getAccounts()) {
					if (StringUtils.equalsIgnoreCase(StringUtils.trim(platformAccountId),
							StringUtils.trim(bundle.getPlatformAccountId()))) {
						return shouldConsider;
					}
				}
			}
			shouldConsider = false;
		}

		return shouldConsider;
	}

	protected Set<String> extractWordsFromName(String name) {
		String newNme = SPACE_PATTERN.matcher(ALPHABETIC_PATTERN.matcher(name).replaceAll(SPACE)).replaceAll(SPACE)
				.trim();
		return new HashSet<>(Arrays.asList(SINGLE_SPACE_PATTERN.split(newNme)));
	}

	protected List<Match> matchBundle(String word, Bundle bundle) {
		List<Match> result = matchAddresses(word, bundle.getAddresses());
		result.addAll(matchUser(word, bundle.getUser()));
		return result;
	}

	protected abstract List<Match> matchAddress(String word, Address address);

	protected abstract String getFilterId();

	protected List<Match> matchUser(String word, User user) {
		final List<Match> result = new ArrayList<>();

		if (wordMatches(word, user.getFirstname()))
			result.add(match(word, user, FIELD_FIRSTNAME));
		if (wordMatches(word, user.getLastname()))
			result.add(match(word, user, FIELD_LASTNAME));
		if (wordMatches(word, user.getUsername()))
			result.add(match(word, user, FIELD_USERNAME));
		if (wordMatches(word, prepareMail(user.getMail())))
			result.add(match(word, user, FIELD_MAIL));

		return result;
	}

	protected String prepareMail(String mail) {
		if (mail == null)
			return null;
		for (String curMaskedDomain : MASKED_ADDRESS_DOMAINS)
			if (mail.toLowerCase().contains(curMaskedDomain))
				return null;

		int index = mail.lastIndexOf(".");
		if (index < 0)
			return mail;

		return mail.substring(0, index);
	}

	protected List<Match> matchAddresses(String word, List<Address> addresses) {
		final List<Match> result = new ArrayList<>();
		for (Address cur : addresses)
			result.addAll(matchAddress(word, cur));
		return result;
	}

	private boolean anyCountryAndDistanceMatch(Bundle bundle) {
		for (Address cur : bundle.getAddresses())
			if (countryMatches(cur) && distanceMatches(cur))
				return true;

		return false;
	}

	protected Match match(String word, Address address, String field) {
		return match(word, ENTITY_ADDRESS, String.valueOf(address.getExternalId()), field);
	}

	protected Match match(String word, User user, String field) {
		return match(word, ENTITY_USER, String.valueOf(user.getExternalId()), field);
	}

	protected Match match(String word, String platformAccountId, String field) {
		return match(word, ENTITY_USER, platformAccountId, field);
	}

	protected Match match(String word, String entity, String id, String field) {
		final String message = MessageFormat.format(MESSAGE, getFilterId(), name, entity, id, field, word);
		return new Match(getFilterId(), name, message);
	}

	protected boolean wordMatches(String word, String fieldValue) {
		if (StringUtils.isBlank(fieldValue))
			return false;

		boolean match = doesWordMatch(word, fieldValue);

		if (!match) {
			String newFieldValue = replaceUmlautes(fieldValue);
			if (!StringUtils.equals(fieldValue, newFieldValue)) {
				match = doesWordMatch(word, newFieldValue);
			}
		}

		return match;
	}

	protected boolean doesWordMatch(String word, String fieldValue) {
		return SPACE_PATTERN
				.matcher(ALPHABETIC_PATTERN.matcher(SPACE + fieldValue.toLowerCase() + SPACE).replaceAll(SPACE))
				.replaceAll(SPACE).contains(SPACE + word.toLowerCase() + SPACE);
	}

	protected boolean countryMatches(Address address) {
		if (StringUtils.isBlank(this.country))
			return true;
		if (StringUtils.isBlank(address.getCountry()))
			return true;

		return address.getCountry().equalsIgnoreCase(this.country);
	}

	// getters and setters

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setPlatformAccountGroupIds(Long[] platformAccountGroupIds) {
		this.platformAccountGroupIds = platformAccountGroupIds;
	}

	public Long[] getPlatformAccountGroupIds() {
		return platformAccountGroupIds;
	}

	private boolean distanceMatches(Address cur) {

		if (StringUtils.isBlank(cur.getCountry()) || StringUtils.isBlank(cur.getZip()) || StringUtils.isBlank(country)
				|| StringUtils.isBlank(zip))
			return true;

		int maxDistance = Config.getZipMaxDistance();

		String numericZip = NUMBERS_PATTERN.matcher(cur.getZip()).replaceAll(StringUtils.EMPTY);
		ZipCode curZipCode1 = getZipCode(cur.getCountry(), numericZip);
		ZipCode curZipCode2 = getZipCode(country, zip);

		if (curZipCode1 == null || curZipCode2 == null)
			return true;

		Double distance = Utils.getDistance(curZipCode1.getLatitude(), curZipCode1.getLongitude(),
				curZipCode2.getLatitude(), curZipCode2.getLongitude());

		if (distance <= maxDistance)
			return true;

		return false;
	}

	private ZipCode getZipCode(String countryCode, String zip) {
		ZipCode curZipCode = ZipCodeRepositoryFactory.get().getZipCode(countryCode, zip);
		if (curZipCode == null && !StringUtils.endsWith(zip, "0")) {
			String zipEndingWithZero = StringUtils.substring(zip, 0, zip.length() - 1) + "0";
			curZipCode = ZipCodeRepositoryFactory.get().getZipCode(countryCode, zipEndingWithZero);
		}
		return curZipCode;
	}
	

	protected String replaceUmlautes(String source) {

		String result = source;

		if (StringUtils.isNotBlank(result)) {
			result = result.toLowerCase();
			if (UMLAUT_PATTERN.matcher(result).find()) {
				for (Object[] UMLAUT_PATTERN_REPLACEMENT : UMLAUT_PATTERN_REPLACEMENTS) {
					result = ((Pattern) UMLAUT_PATTERN_REPLACEMENT[0]).matcher(result)
							.replaceAll((String) UMLAUT_PATTERN_REPLACEMENT[1]);
				}
			}
		}

		return result;
	}
	
}
