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

public class EmailFilterTest {

	private final static String DEFAULT_EMAIL = "johnsmith@gmail.com";
	private OrderRepository orderRepository;
	private FilterRepository filterRepository;
	private String platformAccountId;
	private Long[] platformAccountGroupIds;

	protected void resetModelFactories() {
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
	public void simpleMatchOnEmail() {
		final List<Match> result = emailFilter(DEFAULT_EMAIL).match(returnBundle(null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(EmailFilter.FILTER_ID, match.getType());
		assertEquals(DEFAULT_EMAIL, match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchWithSpacesOnEmail() {
		final List<Match> result = emailFilter("   johnsmith@gmail.com   ").match(returnBundle(null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(EmailFilter.FILTER_ID, match.getType());
		assertEquals(DEFAULT_EMAIL, match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchDifferentCase() {
		final List<Match> result = emailFilter("JoHnSmiTh@gmail.com").match(returnBundle(null));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(EmailFilter.FILTER_ID, match.getType());
		assertTrue(DEFAULT_EMAIL.equalsIgnoreCase(match.getValue()));
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void matchWithUmlauts() {

		// a) Umlaut in Filter but address has no umlaut
		List<Match> result = emailFilter("Kreßner@gmail.com").match(returnBundle("Kressner@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("män@gmail.com").match(returnBundle("maen@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("cüt@gmail.com").match(returnBundle("cuet@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("nöt@gmail.com").match(returnBundle("noet@gmail.com"));
		assertEquals(1, result.size());

		// b) Umlaut in order but filter has no umlaut
		result = emailFilter("Kressner@gmail.com").match(returnBundle("Kreßner@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("maen@gmail.com").match(returnBundle("män@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("cuet@gmail.com").match(returnBundle("cüt@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("noet@gmail.com").match(returnBundle("nöt@gmail.com"));
		assertEquals(1, result.size());

		// c) Umlaut in filter and order
		result = emailFilter("Kreßner@gmail.com").match(returnBundle("Kreßner@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("män@gmail.com").match(returnBundle("män@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("cüt@gmail.com").match(returnBundle("cüt@gmail.com"));
		assertEquals(1, result.size());

		result = emailFilter("aeö@gmail.com").match(returnBundle("äoe@gmail.com"));
		assertEquals(1, result.size());

	}

	@Test
	public void realUseCases() {
		assertFilterMatches("n3qp6k0l605c557@marketplace.amazon.co.u", "n3qp6k0l605c557@marketplace.amazon.co.uk");
		assertFilterMatches("@dgfip.finanes.gouv.fr", "personA@dgfip.finanes.gouv.fr");
		assertFilterMatches("Kreßner@dgfip.finanes.gouv.fr", "Kressner@dgfip.finanes.gouv.fr");
		assertFilterMatches("Kressner@dgfip.finanes.gouv.fr", "Kreßner@dgfip.finanes.gouv.fr");
	}

	@Test
	public void simpleNoMatch() {
		final List<Match> result = emailFilter("john_tyler@yahoo.com").match(returnBundle(null));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleMatchPartial() {
		final List<Match> result = emailFilter("smith@gmail.com").match(returnBundle(null));
		assertNotNull(result);
		assertEquals(1, result.size());
	}

	@Test
	public void simpleNoMatchPlatformAccountId() {
		final List<Match> result = emailFilter(DEFAULT_EMAIL).match(returnBundle(null, "amazon.us@dodax.com"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simplePartialMatchPlatformAccountId() {
		final List<Match> result = emailFilter(DEFAULT_EMAIL).match(returnBundle(null, "amazon.uk"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleMatchOnEmailAndPlatformAccountId() {
		final List<Match> result = emailFilter(DEFAULT_EMAIL).match(returnBundle(DEFAULT_EMAIL, platformAccountId));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(EmailFilter.FILTER_ID, match.getType());
		assertEquals(DEFAULT_EMAIL, match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchOnEmailAndPlatformAccountIdWithDifferentCase() {
		final List<Match> result = emailFilter(DEFAULT_EMAIL)
				.match(returnBundle(DEFAULT_EMAIL, platformAccountId.toUpperCase()));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(EmailFilter.FILTER_ID, match.getType());
		assertEquals(DEFAULT_EMAIL, match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void matchGeneratedAmazonMail() {
		final String email = "1ktkjrc1nl0t9fh@marketplace.amazon.com";
		assertTrue(emailFilter(email).match(bundleMail(email)).size() > 0);
		assertTrue(emailFilter("1ktkjrc1nl0t9fh@marketplace.").match(bundleMail(email)).size() > 0);
	}

	@Test
	public void notMatchGeneratedAmazonMail() {
		final String email = "1ktkjrc1nl0t9fh@marketplace.amazon.com";
		assertTrue(emailFilter("@marketplace.amazon.com").match(bundleMail(email)).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon.").match(bundleMail(email)).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail(email)).size() <= 0);
		assertTrue(emailFilter("@marketplace.").match(bundleMail(email)).size() <= 0);

		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.co.uk")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.fr")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.ca")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.de")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.com")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.co.jp")).size() <= 0);
		assertTrue(emailFilter("@m.marketplace.amazon").match(bundleMail("l@m.marketplace.amazon.co.jp")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.it")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.es")).size() <= 0);
		assertTrue(emailFilter("@marketplace.amazon").match(bundleMail("l@marketplace.amazon.com.mx")).size() <= 0);
	}

	@Test
	public void matchMailAddress() {
		assertTrue(emailFilter("@latop.at").match(bundleMail("sven@latop.at")).size() > 0);
		assertTrue(emailFilter("sven@latop.at").match(bundleMail("sven@latop.at")).size() > 0);
	}

	// helpers

	private void assertFilterMatches(String filterMail, String mail) {
		assertTrue(emailFilter(filterMail).match(returnBundle(mail)).size() > 0);
	}

	private Filter emailFilter(String name) {
		return filterRepository.saveEmailFilter(name, null, platformAccountGroupIds);
	}

	private Bundle bundleMail(String email) {
		return returnBundle(email, null);
	}

	Bundle returnBundle(String email) {
		return returnBundle(email, null);
	}

	private Bundle returnBundle(String email, String platformAccountId) {
		if (email == null)
			email = DEFAULT_EMAIL;

		return orderRepository.newBundle(orderRepository.newUser(55L, "John", "Stockton", "sabfisch", email),
				Arrays.asList(orderRepository.newAddress(44L, "Julia", "Mueller", "SaFi GmbH", "Madison Avenue",
						"London, 17000", "Apartment 25", "1010", "London", "GB", "+43")),
				platformAccountId);
	}

}
