package model.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.order.Bundle;
import model.order.OrderRepository;
import model.order.OrderRepositoryFactory;
import model.platformaccountgroup.PlatformAccountGroup;
import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.platformaccountgroup.TestPlatformAccountGroupCreator;

public class PhoneFilterTest {
	private final static String DEFAULT_PHONE = "6609049071";
	private OrderRepository orderRepository;
	private FilterRepository filterRepository;
	private String platformAccountId;
	private Long[] platformAccountGroupIds;

	private static void resetModelFactories() {
		FilterRepositoryFactory.FACTORY = FilterRepositoryFactory
				.factoryCaching(FilterRepositoryFactory.factoryMemory());
		PlatformAccountGroupRepositoryFactory.FACTORY = PlatformAccountGroupRepositoryFactory
				.factoryCaching(PlatformAccountGroupRepositoryFactory.factoryMemory());
	}

	@Before
	public void before() {
		resetModelFactories();
		this.orderRepository = OrderRepositoryFactory.get();
		this.filterRepository = FilterRepositoryFactory.get();
		PlatformAccountGroup group = TestPlatformAccountGroupCreator.createPlatformAccountGroup();
		platformAccountGroupIds = new Long[] { group.getId() };
		platformAccountId = group.getAccounts()[0];
	}

	@After
	public void after() {
		this.orderRepository = null;
		this.filterRepository = null;
		resetModelFactories();
	}

	// tests

	@Test
	public void simpleMatchOnPhone() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundle(null, null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, match.getType());
		assertEquals(DEFAULT_PHONE, match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchOnPhoneThirdAddressPhone() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundleMultipleAddressess(null, null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, match.getType());
		assertEquals(DEFAULT_PHONE, match.getValue());
		assertTrue(match.getMessage().contains("77"));
	}

	@Test
	public void simpleMatchWithSpacesOnPhone() {
		final List<Match> result = phoneFilter("   6609049071   ").match(returnBundle(null, null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, match.getType());
		assertEquals(DEFAULT_PHONE, match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchWithCountryCodeAndPlusSign() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundle("+43" + DEFAULT_PHONE, null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, match.getType());
		assertTrue(DEFAULT_PHONE.equals(match.getValue()));
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchWithSpacesAndSeparator() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundle("+43 / 660 904 90 71", null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, match.getType());
		assertTrue(DEFAULT_PHONE.equals(match.getValue()));
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchWithCountryCodeAndZeroes() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundle("0043" + DEFAULT_PHONE, null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, match.getType());
		assertTrue(DEFAULT_PHONE.equals(match.getValue()));
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchContainedInMiddle() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE)
				.match(returnBundle("23409823" + DEFAULT_PHONE + "37437474", null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, match.getType());
		assertTrue(DEFAULT_PHONE.equals(match.getValue()));
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleNoMatch() {
		final List<Match> result = phoneFilter("777 444 5656").match(returnBundle(null, null));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleNoMatchPartial() {
		final List<Match> result = phoneFilter("6609049099999").match(returnBundle(null, null));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleNoMatchPlatformAccountId() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundle(DEFAULT_PHONE, "amazon.us@dodax.com"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simplePartialMatchPlatformAccountId() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundle(DEFAULT_PHONE, "amazon.uk"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleMatchPlatformAccountIdWithDifferentCase() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE)
				.match(returnBundle(DEFAULT_PHONE, platformAccountId.toUpperCase()));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match phoneMatch = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, phoneMatch.getType());
		assertTrue(DEFAULT_PHONE.equals(phoneMatch.getValue()));
		assertTrue(phoneMatch.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchOnPhoneAndPlatformAccountId() {
		final List<Match> result = phoneFilter(DEFAULT_PHONE).match(returnBundle(DEFAULT_PHONE, platformAccountId));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match phoneMatch = result.get(0);
		assertEquals(PhoneFilter.FILTER_ID, phoneMatch.getType());
		assertTrue(DEFAULT_PHONE.equals(phoneMatch.getValue()));
		assertTrue(phoneMatch.getMessage().contains("55"));
	}

	@Test
	public void notMatchGeneratedAmazonMail() {
		final String email = "546875216574876@marketplace.amazon.com";
		assertTrue(phoneFilter("546875216574876").match(mailBundle(email)).size() <= 0);
	}

	@Test
	public void notMatchGeneratedAmazonMailJapan() {
		final String email = "546875216574876@m.marketplace.amazon.co.jp";
		assertTrue(phoneFilter("546875216574876").match(mailBundle(email)).size() <= 0);
	}

	// helpers

	private PhoneFilter phoneFilter(String name) {
		return filterRepository.savePhoneFilter(name, null, platformAccountGroupIds);
	}

	private Bundle mailBundle(String email) {
		return bundle(null, null, email);
	}

	private Bundle returnBundle(String phone, String platformAccountId) {
		return bundle(phone, platformAccountId, null);
	}

	private Bundle bundle(String phone, String platformAccountId, String mail) {
		if (mail == null)
			mail = "john@stockton.com";
		if (phone == null)
			phone = DEFAULT_PHONE;

		return orderRepository.newBundle(orderRepository.newUser(44L, "John", "Stockton", "sabfisch", mail),
				Arrays.asList(orderRepository.newAddress(55L, "Julia", "Mueller", "SaFi GmbH", "Madison Avenue",
						"London, 17000", "Apartment 25", "1010", "London", "GB", phone)),
				platformAccountId);
	}

	private Bundle returnBundleMultipleAddressess(String phone, String platformAccountId) {
		if (phone == null)
			phone = DEFAULT_PHONE;

		return orderRepository.newBundle(
				orderRepository.newUser(44L, "John", "Stockton", "sabfisch", "john@stockton.com"),
				Arrays.asList(
						orderRepository.newAddress(55L, "Julia", "Mueller", "SaFi GmbH", "Madison Avenue",
								"London, 17000", "Apartment 25", "1010", "London", "GB", "777 888 9999"),
						orderRepository.newAddress(66L, "Julia", "Mueller", "SaFi GmbH", "Madison Avenue",
								"London, 17000", "Apartment 25", "1010", "London", "GB", "444 555 9999"),
						orderRepository.newAddress(77L, "Julia", "Mueller", "SaFi GmbH", "Madison Avenue",
								"London, 17000", "Apartment 25", "1010", "London", "GB", phone)),
				platformAccountId);
	}

}
