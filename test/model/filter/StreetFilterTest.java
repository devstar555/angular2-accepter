package model.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
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

public class StreetFilterTest {

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
		final User user = orderRepository.newUser(1L, "f", "l", "u", "@");
		final Address address1 = orderRepository.newAddress(1L, "f", "l", "c", "damm", "stock", "a", "1", "C", "A",
				"0");
		final Address address2 = orderRepository.newAddress(1L, "f", "l", "c", "allee", "ebene", "a", "1", "C", "A",
				"0");
		final Bundle bundle = orderRepository.newBundle(user, Arrays.asList(address1, address2), null);

		// should not match
		assertTrue(streetFilter("damm allee").match(bundle).size() <= 0);
		assertTrue(streetFilter("damm ebene").match(bundle).size() <= 0);
		assertTrue(streetFilter("allee stock").match(bundle).size() <= 0);

		// should match
		assertTrue(streetFilter("damm").match(bundle).size() > 0);
		assertTrue(streetFilter("allee").match(bundle).size() > 0);
		assertTrue(streetFilter("damm stock").match(bundle).size() > 0);
		assertTrue(streetFilter("allee ebene").match(bundle).size() > 0);
	}

	@Test
	public void matchOnPartialString() {
		assertTrue(streetFilter("boule", null).match(bundleAddress1("boulevard spain")).size() <= 0);
	}

	@Test
	public void matchOnPartialStringOtherWord() {
		assertTrue(streetFilter("spai", null).match(bundleAddress1("boulevard spain")).size() <= 0);
	}

	@Test
	public void noMatchOnPartialSeparateWords() {
		assertTrue(streetFilter("evardspa", null).match(bundleAddress1("boulevard spain")).size() <= 0);
	}

	@Test
	public void simpleMatchOnAddress1() {
		final List<Match> result = streetFilter("Spain", null).match(bundleAddress1("boulevard spain"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(StreetFilter.FILTER_ID, match.getType());
		assertEquals("spain", match.getValue());
		assertTrue(match.getMessage().contains("44"));
	}

	@Test
	public void simpleMatchOnAddress2() {
		final List<Match> result = streetFilter("Spain", null).match(bundleAddress2("boulevard spain"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(StreetFilter.FILTER_ID, match.getType());
		assertEquals("spain", match.getValue());
		assertTrue(match.getMessage().contains("44"));
	}

	@Test
	public void simpleMatchOnAddress3() {
		final List<Match> result = streetFilter("Spain", null).match(bundleAddress3("boulevard spain"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(StreetFilter.FILTER_ID, match.getType());
		assertEquals("spain", match.getValue());
		assertTrue(match.getMessage().contains("44"));
	}

	@Test
	public void simpleNoMatchOnName() {
		assertTrue(streetFilter("boulevard spain", null).match(bundleLastname("boulevard spain")).size() <= 0);
	}

	@Test
	public void simpleNoMatch() {
		assertTrue(streetFilter("doesNotMatch", null).match(bundle()).size() <= 0);
	}

	@Test
	public void matchMultipleWordsOnAddress() {
		assertTrue(streetFilter("U Studansky", null).match(bundle()).size() > 0);
		assertTrue(streetFilter("Stock", null).match(bundle()).size() > 0);
	}

	@Test
	public void dontMatchMultipleWords() {
		assertEquals(0, streetFilter("Studansky NoMatch", null).match(bundle()).size());
		assertEquals(0, streetFilter("Sabine Stock NoMatch", null).match(bundle()).size());
		assertEquals(0, streetFilter("sabfisch Studansky sabsi NoMatch", null).match(bundle()).size());
	}

	@Test
	public void matchNameSpecialCharacters() {
		final Filter filter = streetFilter("mueller", null);
		assertTrue(filter.match(bundleAddress1("mueller")).size() > 0);
		assertTrue(filter.match(bundleAddress1(" mueller ")).size() > 0);
		assertTrue(filter.match(bundleAddress1(" \t mueller\t\t ")).size() > 0);
		assertTrue(filter.match(bundleAddress1("!mueller-")).size() > 0);
		assertTrue(filter.match(bundleAddress1("@mueller?")).size() > 0);
		assertTrue(filter.match(bundleAddress1("-mueller=")).size() > 0);
		assertTrue(filter.match(bundleAddress1("()|mueller[]{}")).size() > 0);
		assertTrue(filter.match(bundleAddress1(" =2= mueller ")).size() > 0);
		assertTrue(filter.match(bundleAddress1("1mueller8")).size() > 0);
		assertTrue(filter.match(bundleAddress1("mueller1987")).size() > 0);
	}

	@Test
	public void realUseCases() {
		assertFilterMatches("An der Alster", "An der Alster 15");
		assertFilterNotMatches("An der Alster", "An der Donau 12/a");
		assertFilterNotMatches("An der Donau", "An der Alster 15");
		assertFilterMatches("Franz Albert Strasse", "Franz-Albert-Strasse 2");
		assertFilterMatches("Landgraf Philipp Str", "Landgraf-Philipp-Strasse");
		assertFilterMatches("M e ciliberti", "m.e.ciliberti 16");
		assertFilterMatches("60 Chiswell Street", "60 Chiswell Street");
	}

	@Test
	public void matchSplit() {
		final Bundle bundleMatch = customBundle("Julia", "Tester", "Via Luigi Angrisani 21", "84132", "Salerno");
		final Bundle bundleNoMatch = customBundle("Julia", "Tester", "Via Luigi Angrisani 22", "84132", "Salerno");
		final Filter filter = streetFilter("Via Luigi Angrisani 21  84132 Salerno");
		assertTrue(filter.match(bundleMatch).size() > 0);
		assertTrue(filter.match(bundleNoMatch).size() <= 0);
	}

	@Test
	public void streetNumbers() {
		final String userAddress = "Waldring 47";

		assertFilterMatches("47", userAddress);
		assertFilterMatches("WaldrING", userAddress);
		assertFilterMatches("47 WaldRIng", userAddress);
		assertFilterMatches("WaldRing 47", userAddress);
		assertFilterMatches("Waldring 47", userAddress);
		assertFilterMatches("47 waldring", userAddress);

		assertFilterNotMatches("7 waldring", userAddress);
		assertFilterNotMatches("waldring 7", userAddress);
		assertFilterNotMatches("Waldring 48", userAddress);
		assertFilterNotMatches("Waldrinr 47", userAddress);
		assertFilterNotMatches("waldringg", userAddress);
		assertFilterNotMatches("478", userAddress);
		assertFilterNotMatches("wald4", userAddress);
	}

	private void assertFilterMatches(String filterName, String address2) {
		assertTrue(streetFilter(filterName, null).match(bundleAddress2(address2)).size() > 0);
	}

	private void assertFilterNotMatches(String filterName, String address2) {
		assertTrue(streetFilter(filterName, null).match(bundleAddress2(address2)).size() <= 0);
	}

	@Test
	public void matchNameWithAccent() {
		final Filter filter = streetFilter("10 probó", null);
		assertTrue(filter.match(bundleAddress1("10 probó")).size() > 0);
		assertTrue(filter.match(bundleAddress1("!probó@10")).size() > 0);
	}

	@Test
	public void matchNameUmlaute() {
		Filter filter = streetFilter("Kreßner 20", null);
		assertTrue(filter.match(bundleAddress1("Kreßner 20")).size() > 0);
		assertTrue(filter.match(bundleAddress1("Kressner 20")).size() > 0);

		filter = streetFilter("müller 20", null);
		assertTrue(filter.match(bundleAddress1("müller 20")).size() > 0);
		assertTrue(filter.match(bundleAddress1("___müller?20")).size() > 0);

		filter = streetFilter("cät 20", null);
		assertTrue(filter.match(bundleAddress1("cät 20")).size() > 0);
		assertTrue(filter.match(bundleAddress1("caet 20")).size() > 0);

		filter = streetFilter("cüt 20", null);
		assertTrue(filter.match(bundleAddress1("cüt 20")).size() > 0);
		assertTrue(filter.match(bundleAddress1("cuet 20")).size() > 0);

		filter = streetFilter("cöt 20", null);
		assertTrue(filter.match(bundleAddress1("cöt 20")).size() > 0);
		assertTrue(filter.match(bundleAddress1("coet 20")).size() > 0);
	}

	@Test
	public void matchOrderUmlaute() {
		Filter filter = streetFilter("Kressner 20", null);
		assertTrue(filter.match(bundleAddress1("Kreßner 20")).size() > 0);

		filter = streetFilter("mueller 20", null);
		assertTrue(filter.match(bundleAddress1("müller 20")).size() > 0);

		filter = streetFilter("caet 20", null);
		assertTrue(filter.match(bundleAddress1("cät 20")).size() > 0);

		filter = streetFilter("cuet 20", null);
		assertTrue(filter.match(bundleAddress1("cüt 20")).size() > 0);

		filter = streetFilter("coet 20", null);
		assertTrue(filter.match(bundleAddress1("cöt 20")).size() > 0);
	}

	@Test
	public void matchNameUnicode() {
		final Filter filter = streetFilter("高原文彦", null);
		assertTrue(filter.match(bundleAddress1("高原文彦")).size() > 0);
		assertTrue(filter.match(bundleAddress1("!2@-高原文彦")).size() > 0);
	}

	@Test
	public void matchAddress1WithCountry() {
		assertTrue(streetFilter("U Studansky", "DE").match(bundle()).size() > 0);
	}

	@Test
	public void dontMatchNameWithCountry() {
		assertEquals(0, streetFilter("sabine", "UY").match(bundle()).size());
		assertEquals(0, streetFilter("sabine", "AR").match(bundle()).size());
	}

	@Test
	public void matchAllFields() {
		assertTrue(streetFilter("studansky").match(bundle()).size() > 0); // address1
		assertTrue(streetFilter("stock").match(bundle()).size() > 0); // address2
		assertTrue(streetFilter("tuer").match(bundle()).size() > 0); // address3
	}

	@Test
	public void noMatchFields() {

		// user
		assertTrue(streetFilter("Sabine").match(bundle()).size() == 0); // firstname
		assertTrue(streetFilter("Fischer").match(bundle()).size() == 0); // lastname
		assertTrue(streetFilter("sabfisch").match(bundle()).size() == 0); // username
		assertTrue(streetFilter("sabsi").match(bundle()).size() == 0); // mail

		// address
		assertTrue(streetFilter("Julia").match(bundle()).size() == 0); // firstname
		assertTrue(streetFilter("Mueller").match(bundle()).size() == 0); // lastname
		assertTrue(streetFilter("SaFi GmbH").match(bundle()).size() == 0); // company
		assertTrue(streetFilter("Wien").match(bundle()).size() == 0); // city
		assertTrue(streetFilter("DE").match(bundle()).size() == 0); // country
	}

	@Test
	public void simpleNoMatchPlatformAccountId() {
		final List<Match> result = streetFilter("spain")
				.match(bundlePlatformAccountId("boulevard spain", "amazon.us@dodax.com"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simplePartialMatchPlatformAccountId() {
		final List<Match> result = streetFilter("spain").match(bundlePlatformAccountId("boulevard spain", "amazon.uk"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleMatchPlatformAccountIdWithDifferentCase() {
		final List<Match> result = streetFilter("spain")
				.match(bundlePlatformAccountId("boulevard spain", platformAccountId.toUpperCase()));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match streetMatch = result.get(0);
		assertEquals(StreetFilter.FILTER_ID, streetMatch.getType());
		assertTrue("spain".equals(streetMatch.getValue()));
		assertTrue(streetMatch.getMessage().contains("44"));
	}

	@Test
	public void simpleMatchOnAddress1AndPlatformAccountId() {
		final List<Match> result = streetFilter("spain")
				.match(bundlePlatformAccountId("boulevard spain", platformAccountId));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match streetMatch = result.get(0);
		assertEquals(StreetFilter.FILTER_ID, streetMatch.getType());
		assertTrue("spain".equals(streetMatch.getValue()));
		assertTrue(streetMatch.getMessage().contains("44"));
	}

	@Test
	public void notMatchGeneratedAmazonMail() {
		final String email = "1ktkjrc1nl0t9fh@marketplace.amazon.com";
		assertTrue(streetFilter("amazon").match(bundleMail(email)).size() <= 0);
	}

	@Test
	public void expandStr() {
		assertTrue(streetFilter("hafenstr 73").match(bundleAddress1("Hafenstr 73")).size() > 0);
		assertTrue(streetFilter("hafenstr 73").match(bundleAddress1("Hafenstr. 73")).size() > 0);
		assertTrue(streetFilter("hafenstr 73").match(bundleAddress1("Hafenstrasse 73")).size() > 0);
		assertTrue(streetFilter("hafenstr 73").match(bundleAddress1("Hafenstraße 73")).size() > 0);
		assertTrue(streetFilter("hafenstr 73").match(bundleAddress1("Hafenstreet 73")).size() > 0);
	}

	@Test
	public void expandStrSpecial() {
		assertTrue(streetFilter("str 73").match(bundleAddress1("Strasse 73")).size() > 0);
		assertTrue(streetFilter("AstrB 73").match(bundleAddress1("AstrB 73")).size() > 0);
		assertTrue(streetFilter("AstrB 73").match(bundleAddress1("AstrasseB 73")).size() <= 0);
	}

	@Test
	public void expandSt() {
		assertTrue(streetFilter("188 spear st").match(bundleAddress1("188 spear st")).size() > 0);
		assertTrue(streetFilter("188 spear st").match(bundleAddress1("188 spear street")).size() > 0);
		assertTrue(streetFilter("188 spear st").match(bundleAddress1("188 spear straße")).size() > 0);
		assertTrue(streetFilter("188 spear st").match(bundleAddress1("188 spear strasse")).size() > 0);
		assertTrue(streetFilter("188 spear st").match(bundleAddress1("188 spear str.")).size() > 0);
	}

	@Test
	public void expandAve() {
		assertTrue(streetFilter("222 severn ave").match(bundleAddress1("222 severn ave")).size() > 0);
		assertTrue(streetFilter("222 severn ave").match(bundleAddress1("222 severn avenue")).size() > 0);
	}

	@Test
	public void expandRd() {
		assertTrue(streetFilter("18 clev rd").match(bundleAddress1("18 clev rd")).size() > 0);
		assertTrue(streetFilter("18 clev rd").match(bundleAddress1("18 clev road")).size() > 0);
	}

	@Test
	public void explandLn() {
		assertTrue(streetFilter("27 wrights ln").match(bundleAddress1("27 wrights ln")).size() > 0);
		assertTrue(streetFilter("27 wrights ln").match(bundleAddress1("27 wrights lane")).size() > 0);
	}

	@Test
	public void variousExpansions() {
		assertTrue(streetFilter("54 rue de chateaudun").match(bundleAddress1("52_54 RUE DE CHATEAUDUN")).size() > 0);
		assertTrue(streetFilter("9 6 35 akasaka").match(bundleAddress1("9-6-35 Akasaka")).size() > 0);
		assertTrue(streetFilter("aarburgerstr").match(bundleAddress1("aarburgerstr.55")).size() > 0);
		assertTrue(streetFilter("alt seulberg 90a").match(bundleAddress1("alt seulberg 90a")).size() > 0);
		assertTrue(streetFilter("andreas gayk str 7 11").match(bundleAddress1("Andreas-Gayk-Str. 7-11")).size() > 0);
		assertTrue(streetFilter("czumy 3 77").match(bundleAddress1("ul. Gen. Czumy 3/77")).size() > 0);
		assertTrue(streetFilter("川越市笠幡 121 1").match(bundleAddress1("川越市笠幡121-1")).size() > 0);
		assertTrue(streetFilter("am park 1").match(bundleAddress1("Am Park 1")).size() > 0);
		assertTrue(streetFilter("an alster 21").match(bundleAddress1("An der Alster 21")).size() > 0);
	}

	@Test
	public void streetNumberHandling() {
		assertTrue(streetFilter("schönhauser allee 73").match(bundleAddress1("Schönhauser Allee 73 b")).size() > 0);
		assertTrue(streetFilter("schönhauser allee 73").match(bundleAddress1("Schönhauser Allee 73b")).size() > 0);
		assertTrue(streetFilter("schönhauser allee 73").match(bundleAddress1("Schönhauser Allee 73/b")).size() > 0);
		assertTrue(streetFilter("schönhauser allee 73").match(bundleAddress1("Schönhauser Allee 73")).size() > 0);
		assertTrue(streetFilter("schönhauser allee 73").match(bundleAddress1("Schönhauser-Allee-73")).size() > 0);
		assertTrue(streetFilter("schönhauser allee 73").match(bundleAddress1("Schönhauser-Allee 73")).size() > 0);
	}

	@Test
	public void notMatchGeneratedAmazonMailJapan() {
		final String email = "1ktkjrc1nl0t9fh@m.marketplace.amazon.co.jp";
		assertTrue(streetFilter("amazon").match(bundleMail(email)).size() <= 0);
	}

	// helpers

	private Filter streetFilter(String name) {
		return streetFilter(name, null);
	}

	private Filter streetFilter(String name, String country) {
		return filterRepository.saveStreetFilter(name, country, null, null, platformAccountGroupIds);
	}

	private Filter streetFilter(String name, String country, String zip) {
		return filterRepository.saveStreetFilter(name, country, zip, null, platformAccountGroupIds);
	}

	private Bundle bundle() {
		return customBundle(null, null, null, null, null);
	}

	private Bundle bundleLastname(String lastname) {
		return customBundle(null, lastname, null, null, null);
	}

	private Bundle bundleAddress1(String address1) {
		return customBundle(null, null, address1, null, null);
	}

	private Bundle bundleMail(String email) {
		return customBundle(null, null, null, null, null, null, email);
	}

	private Bundle bundleAddress2(String address2) {
		return customBundle(null, null, null, address2, null);
	}

	private Bundle bundleAddress3(String address3) {
		return customBundle(null, null, null, null, address3);
	}

	private Bundle customBundle(String firstname, String lastname, String address1, String address2, String address3) {
		return customBundle(firstname, lastname, address1, address2, address3, null, null);
	}

	private Bundle bundlePlatformAccountId(String address1, String platformAccountId) {
		return customBundle(null, null, address1, null, null, platformAccountId, null);
	}

	private Bundle customBundle(String firstname, String lastname, String address1, String address2, String address3,
			String platformAccountId, String mail) {
		if (firstname == null)
			firstname = "Sabine";
		if (lastname == null)
			lastname = "Fischer";
		if (address1 == null)
			address1 = "U Studansky";
		if (address2 == null)
			address2 = "Stock 2";
		if (address3 == null)
			address3 = "Tuer 4";
		if (mail == null)
			mail = "sabsi@fischer.at";

		return orderRepository.newBundle(orderRepository.newUser(55L, firstname, lastname, "sabfisch", mail),
				Arrays.asList(orderRepository.newAddress(44L, "Julia", "Mueller", "SaFi GmbH", address1, address2,
						address3, "1010", "Wien", "DE", "+43")),
				platformAccountId);
	}

	@Test
	public void matchOutsideMaxDistance() {
		final List<Match> result = streetFilter("spain", "de", "9992").match(bundleAddress1("spain"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}
	
	@Test
	public void matchWithinMaxDistance() {
		final List<Match> result = streetFilter("spain", "de", "1010").match(bundleAddress1("spain"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(StreetFilter.FILTER_ID, match.getType());
		assertEquals("spain", match.getValue());
		assertTrue(match.getMessage().contains("44"));
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
