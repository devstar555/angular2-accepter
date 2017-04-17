package model.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.config.ConfigRepositoryFactory;
import model.order.Address;
import model.order.Bundle;
import model.order.OrderRepository;
import model.order.OrderRepositoryFactory;
import model.order.User;
import model.platformaccountgroup.PlatformAccountGroup;
import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.platformaccountgroup.TestPlatformAccountGroupCreator;
import model.zipcode.TestZipCodeCreator;
import model.zipcode.ZipCodeRepositoryFactory;

public class PersonFilterTest {
	private OrderRepository orderRepository;
	private FilterRepository filterRepository;
	private String platformAccountId;
	private Long[] platformAccountGroupIds;

	private static void resetModelFactories() {
		FilterRepositoryFactory.FACTORY = FilterRepositoryFactory
				.factoryCaching(FilterRepositoryFactory.factoryMemory());
		ZipCodeRepositoryFactory.FACTORY = ZipCodeRepositoryFactory
				.factoryCaching(ZipCodeRepositoryFactory.factoryMemory());
		ConfigRepositoryFactory.FACTORY = ConfigRepositoryFactory
				.factoryCaching(ConfigRepositoryFactory.factoryMemory());
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
		createTestZipCodes();
		TestLoginUserCeator.createHttpBasicAuth();
	}

	@After
	public void after() {
		this.orderRepository = null;
		this.filterRepository = null;
		resetModelFactories();
	}

	// tests

	@Test
	public void crossAddressMatching() {
		final User user = orderRepository.newUser(1L, "herwig", "l", "u", "@");
		final Address address1 = orderRepository.newAddress(1L, "sabine", "l", "c", "a", "a", "a", "1", "C", "A", "0");
		final Address address2 = orderRepository.newAddress(1L, "fischer", "l", "c", "a", "a", "a", "1", "C", "A", "0");
		final Bundle bundle = orderRepository.newBundle(user, Arrays.asList(address1, address2), null);

		// should not match
		assertTrue(personFilter("sabine fischer herwig").match(bundle).size() <= 0);
		assertTrue(personFilter("sabine fischer").match(bundle).size() <= 0);

		// should match
		assertTrue(personFilter("herwig").match(bundle).size() > 0);
		assertTrue(personFilter("sabine").match(bundle).size() > 0);
		assertTrue(personFilter("fischer").match(bundle).size() > 0);
		assertTrue(personFilter("herwig sabine").match(bundle).size() > 0);
		assertTrue(personFilter("herwig fischer").match(bundle).size() > 0);
	}

	@Test
	public void simpleMatch() {
		final List<Match> result = personFilter("Sabine", null).match(bundleFischer());
		assertNotNull(result);
		assertEquals(2, result.size());
		for (int i = 0; i < 2; i++) {
			final Match match = result.get(i);
			assertEquals(PersonFilter.FILTER_ID, match.getType());
			assertEquals("sabine", match.getValue());
			assertTrue(match.getMessage().contains("44") || match.getMessage().contains("55"));
		}
	}

	@Test
	public void simpleDontMatch() {
		final List<Match> result = personFilter("NoMatch", null).match(bundleFischer());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void matchMultipleWords() {
		assertTrue(personFilter("Sabine Sabine", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("Sabine Fischer", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("Sabine sabsi", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("fischer sabine", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("sabfisch Sabine fischer sabsi", null).match(bundleFischer()).size() > 0);
	}

	@Test
	public void dontMatchMultipleWords() {
		assertEquals(0, personFilter("Sabine NoMatch Fischer", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("Sabine sabsi NoMatch", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("NoMatch fischer sabine", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("sabfisch Sabine fischer sabsi NoMatch", null).match(bundleFischer()).size());
	}

	@Test
	public void matchNameSpecialCharacters() {
		final Filter filter = personFilter("mueller", null);
		assertTrue(filter.match(bundle("mueller")).size() > 0);
		assertTrue(filter.match(bundle(" mueller ")).size() > 0);
		assertTrue(filter.match(bundle(" \t mueller\t\t ")).size() > 0);
		assertTrue(filter.match(bundle("!mueller-")).size() > 0);
		assertTrue(filter.match(bundle("@mueller?")).size() > 0);
		assertTrue(filter.match(bundle("-mueller=")).size() > 0);
		assertTrue(filter.match(bundle("()|mueller[]{}")).size() > 0);
		assertTrue(filter.match(bundle(" =2= mueller ")).size() > 0);
		assertTrue(filter.match(bundle("1mueller8")).size() > 0);
		assertTrue(filter.match(bundle("mueller1987")).size() > 0);
	}

	@Test
	public void matchFilterNameWithUmlaute() {

		Filter filter = personFilter("Kreßner", null);
		assertTrue(filter.match(bundle("Kressner")).size() > 0);
		assertTrue(filter.match(bundle("KRESSNER")).size() > 0);
		assertTrue(filter.match(bundle("Kreßner")).size() > 0);
		assertTrue(filter.match(bundle("KREßNER")).size() > 0);

		filter = personFilter("müller", null);
		assertTrue(filter.match(bundle("mueller")).size() > 0);
		assertTrue(filter.match(bundle("MUELLER")).size() > 0);
		assertTrue(filter.match(bundle("müller")).size() > 0);
		assertTrue(filter.match(bundle("MÜLLER")).size() > 0);
		assertTrue(filter.match(bundle("!2@-MÜLLER")).size() > 0);

		filter = personFilter("män", null);
		assertTrue(filter.match(bundle("maen")).size() > 0);
		assertTrue(filter.match(bundle("MAEN")).size() > 0);
		assertTrue(filter.match(bundle("män")).size() > 0);
		assertTrue(filter.match(bundle("MÄN")).size() > 0);

		filter = personFilter("nöt", null);
		assertTrue(filter.match(bundle("noet")).size() > 0);
		assertTrue(filter.match(bundle("NOET")).size() > 0);
		assertTrue(filter.match(bundle("nöt")).size() > 0);
		assertTrue(filter.match(bundle("NÖT")).size() > 0);
	}

	@Test
	public void matchOrderWithUmlaute() {

		Filter filter = personFilter("Kressner", null);
		assertTrue(filter.match(bundle("Kressner")).size() > 0);
		assertTrue(filter.match(bundle("KRESSNER")).size() > 0);
		assertTrue(filter.match(bundle("Kreßner")).size() > 0);
		assertTrue(filter.match(bundle("KREßNER")).size() > 0);

		filter = personFilter("mueller", null);
		assertTrue(filter.match(bundle("mueller")).size() > 0);
		assertTrue(filter.match(bundle("MUELLER")).size() > 0);
		assertTrue(filter.match(bundle("müller")).size() > 0);
		assertTrue(filter.match(bundle("MÜLLER")).size() > 0);
		assertTrue(filter.match(bundle("!2@-MÜLLER")).size() > 0);

		filter = personFilter("maen", null);
		assertTrue(filter.match(bundle("maen")).size() > 0);
		assertTrue(filter.match(bundle("MAEN")).size() > 0);
		assertTrue(filter.match(bundle("män")).size() > 0);
		assertTrue(filter.match(bundle("MÄN")).size() > 0);

		filter = personFilter("noet", null);
		assertTrue(filter.match(bundle("noet")).size() > 0);
		assertTrue(filter.match(bundle("NOET")).size() > 0);
		assertTrue(filter.match(bundle("nöt")).size() > 0);
		assertTrue(filter.match(bundle("NÖT")).size() > 0);

	}


	@Test
	public void matchNameUnicode() {
		final Filter filter = personFilter("高原文彦", null);
		assertTrue(filter.match(bundle("高原文彦")).size() > 0);
		assertTrue(filter.match(bundle("!2@-高原文彦")).size() > 0);
	}

	@Test
	public void matchNameWithCountry() {
		assertTrue(personFilter("sabine", "DE").match(bundleFischer()).size() > 0);
	}

	@Test
	public void dontMatchNameWithCountry() {
		assertEquals(0, personFilter("sabine", "UY").match(bundleFischer()).size());
		assertEquals(0, personFilter("sabine", "AT").match(bundleFischer()).size());
	}

	@Test
	public void realUseCases() {
		assertFilterMatches("Dr Jutta Gratopp", "Dr. Jutta Gratopp");
	}

	private void assertFilterMatches(String filterName, String lastname) {
		assertTrue(personFilter(filterName, null).match(bundle(lastname)).size() > 0);
	}

	@Test
	public void matchNameMailCompany() {
		assertTrue(personFilter("sabine", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("fischer", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("sabsi", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("sabfisch", null).match(bundleFischer()).size() > 0);
		assertTrue(personFilter("safi", null).match(bundleFischer()).size() > 0);
	}

	@Test
	public void dontMatchNameMailWrongly() {
		assertEquals(0, personFilter("sabi", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("fisch", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("at", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("sabs", null).match(bundleFischer()).size());
	}

	@Test
	public void dontMatchNonNameFields() {
		assertEquals(0, personFilter("Leerstrasse", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("eerstrass", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("wien", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("de", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("tuer", null).match(bundleFischer()).size());
		assertEquals(0, personFilter("stock", null).match(bundleFischer()).size());
	}

	@Test
	public void dontMatchMinimumBundle() {
		assertEquals(0, personFilter("sabine", null).match(minimalBundle()).size());
		assertEquals(0, personFilter("sabine", null).match(minimalBundleWithAddress()).size());
	}

	@Test
	public void matchAllFields() {
		assertTrue(personFilter("Sabine").match(bundleFischer()).size() > 0); // name
		assertTrue(personFilter("Fischer").match(bundleFischer()).size() > 0); // lastName
		assertTrue(personFilter("sabfisch").match(bundleFischer()).size() > 0); // userName
		assertTrue(personFilter("sabsi").match(bundleFischer()).size() > 0); // mail
		assertTrue(personFilter("SaFi GmbH").match(bundleFischer()).size() > 0); // company
	}

	@Test
	public void matchAllFieldsExtended() {
		final Bundle bundle = bundleAllFieldsDifferent();

		// user
		assertTrue(personFilter("ufirst").match(bundle).size() > 0); // firstname
		assertTrue(personFilter("ulast").match(bundle).size() > 0); // lastname
		assertTrue(personFilter("uname").match(bundle).size() > 0); // username
		assertTrue(personFilter("umail").match(bundle).size() > 0); // username

		// address
		assertTrue(personFilter("afirst").match(bundle).size() > 0); // firstname
		assertTrue(personFilter("alast").match(bundle).size() > 0); // lastname
		assertTrue(personFilter("acompany").match(bundle).size() > 0); // company

		// address (no match)
		assertTrue(personFilter("astreeta").match(bundle).size() <= 0); // street1
		assertTrue(personFilter("astreetb").match(bundle).size() <= 0); // street2
		assertTrue(personFilter("astreetc").match(bundle).size() <= 0); // street3
		assertTrue(personFilter("acity").match(bundle).size() <= 0); // street3

	}

	@Test
	public void nomatchFields() {
		assertTrue(personFilter("Leerstrasse").match(bundleFischer()).size() == 0); // address1
		assertTrue(personFilter("Stock").match(bundleFischer()).size() == 0); // address2
		assertTrue(personFilter("Tuer").match(bundleFischer()).size() == 0); // address3

	}

	@Test
	public void simpleMatchOnFirstNameAndPlatformAccountId() {
		final List<Match> result = personFilter("Sabine", null).match(bundle(null, platformAccountId));
		assertNotNull(result);
		assertEquals(2, result.size());
		for (int i = 0; i < 2; i++) {
			final Match match = result.get(i);
			assertEquals(PersonFilter.FILTER_ID, match.getType());
			assertEquals("sabine", match.getValue());
			assertTrue(match.getMessage().contains("44") || match.getMessage().contains("55"));
		}
	}

	@Test
	public void simpleNoMatchPlatformAccountId() {
		final List<Match> result = personFilter("Joseph").match(bundle("Shephard", "amazon.us@dodax.com"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simplePartialMatchPlatformAccountId() {
		final List<Match> result = personFilter("Joseph").match(bundle("Shephard", "amazon.uk"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleMatchPlatformAccountIdWithDifferentCase() {
		final List<Match> result = personFilter("Sabine").match(bundle(null, platformAccountId.toUpperCase()));
		assertNotNull(result);
		assertEquals(2, result.size());
		for (int i = 0; i < 2; i++) {
			final Match match = result.get(i);
			assertEquals(PersonFilter.FILTER_ID, match.getType());
			assertEquals("sabine", match.getValue());
			assertTrue(match.getMessage().contains("44") || match.getMessage().contains("55"));
		}
	}

	@Test
	public void notMatchGeneratedAmazonMail() {
		final String email = "1ktkjrc1nl0t9fh@marketplace.amazon.com";
		assertTrue(personFilter("amazon").match(bundleMail(email)).size() <= 0);
		assertTrue(personFilter("marketplace").match(bundleMail(email)).size() <= 0);
		assertTrue(personFilter("com").match(bundleMail(email)).size() <= 0);
		assertTrue(personFilter("fh").match(bundleMail(email)).size() <= 0);
		assertTrue(personFilter("nl").match(bundleMail(email)).size() <= 0);
		assertTrue(personFilter("ktkjrc").match(bundleMail(email)).size() <= 0);

		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.co.uk")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.fr")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.ca")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.de")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.com")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.co.jp")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@m.marketplace.amazon.co.jp")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.it")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.es")).size() <= 0);
		assertTrue(personFilter("amazon").match(bundleMail("l@marketplace.amazon.com.mx")).size() <= 0);
	}

	@Test
	public void matchMailAddress() {
		assertTrue(personFilter("latop").match(bundleMail("sven@latop.at")).size() > 0);
		assertTrue(personFilter("sven").match(bundleMail("sven@latop.at")).size() > 0);
	}

	// helpers

	private Filter personFilter(String name) {
		return filterRepository.savePersonFilter(name, null, null, null, platformAccountGroupIds);
	}

	private Filter personFilter(String name, String country) {
		return filterRepository.savePersonFilter(name, country, null, null, platformAccountGroupIds);
	}

	private Filter personFilter(String name, String country, String zip) {
		return filterRepository.savePersonFilter(name, country, zip, null, platformAccountGroupIds);
	}

	private Bundle bundleFischer() {
		return bundle("Fischer", null);
	}

	private Bundle bundleMail(String email) {
		return orderRepository.newBundle(orderRepository.newUser(55L, "Sabine", "Fischer", "sabfisch", email),
				Arrays.asList(orderRepository.newAddress(44L, "Sabine", "Fischer", "SaFi GmbH", "Leerstrasse 15",
						"Stock 2", "Tuer 4", "1010", "Wien", "DE", "+43")),
				null);
	}

	private Bundle bundle(String lastname) {
		return bundle(lastname, null);
	}

	private Bundle bundle(String lastname, String platformAccountId) {
		return orderRepository
				.newBundle(orderRepository.newUser(55L, "Sabine", lastname, "sabfisch", "sabsi@fischer.at"),
						Arrays.asList(orderRepository.newAddress(44L, "Sabine", "Fischer", "SaFi GmbH",
								"Leerstrasse 15", "Stock 2", "Tuer 4", "1010", "Wien", "DE", "+43")),
						platformAccountId);
	}

	private Bundle bundleAllFieldsDifferent() {
		return orderRepository.newBundle(orderRepository.newUser(55L, "UFirst", "ULast", "UName", "UMail@domain.at"),
				Arrays.asList(orderRepository.newAddress(44L, "AFirst", "ALast", "ACompany", "AStreetA 15",
						"AStreetB 2", "AStreetC 4", "1010", "ACity", "DE", "+43")),
				platformAccountId);
	}

	private Bundle minimalBundle() {
		return orderRepository.newBundle(minimalUser(), Collections.emptyList(), null);
	}

	private Bundle minimalBundleWithAddress() {
		return orderRepository.newBundle(minimalUser(), Arrays.asList(minimalAddress()), null);
	}

	private Address minimalAddress() {
		return orderRepository.newAddress(55L, null, null, null, null, null, null, null, null, null, null);
	}

	private User minimalUser() {
		return orderRepository.newUser(55L, null, null, null, null);
	}

	@Test
	public void matchWithinMaxDistance() {
		final List<Match> result = personFilter("Sabine", "de", "1010").match(bundleFischer());
		assertNotNull(result);
		assertEquals(2, result.size());
		for (int i = 0; i < 2; i++) {
			final Match match = result.get(i);
			assertEquals(PersonFilter.FILTER_ID, match.getType());
			assertEquals("sabine", match.getValue());
			assertTrue(match.getMessage().contains("44") || match.getMessage().contains("55"));
		}
	}

	@Test
	public void matchOutsideMaxDistance() {
		final List<Match> result = personFilter("Sabine", "de", "9992").match(bundleFischer());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	private void createTestZipCodes() {
		TestZipCodeCreator.createZipCode("1010", "Wien", "DE", "48.2077", "16.3705");
		TestZipCodeCreator.createZipCode("1020", "Wien", "DE", "48.2167", "16.4000");
		TestZipCodeCreator.createZipCode("1030", "Wien", "DE", "48.1981", "16.3948");
		TestZipCodeCreator.createZipCode("1040", "Wien", "DE", "48.192", "16.3671");
		TestZipCodeCreator.createZipCode("1050", "Wien", "DE", "48.192", "16.3671");
		TestZipCodeCreator.createZipCode("9992", "Iselsberg-Stronach", "DE", "46.8357", "12.8497");
	}
}
