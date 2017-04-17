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

public class CompanyFilterTest {

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
		assertTrue(companyFilter("sabine fischer herwig").match(bundle).size() <= 0);
		assertTrue(companyFilter("sabine fischer").match(bundle).size() <= 0);

		// should match
		assertTrue(companyFilter("herwig").match(bundle).size() > 0);
		assertTrue(companyFilter("sabine").match(bundle).size() > 0);
		assertTrue(companyFilter("fischer").match(bundle).size() > 0);
		assertTrue(companyFilter("herwig sabine").match(bundle).size() > 0);
		assertTrue(companyFilter("herwig fischer").match(bundle).size() > 0);
	}

	@Test
	public void simpleMatchOnAddress1() {
		final List<Match> result = companyFilter("Accenture", null).match(bundleAddress1("accenture"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(CompanyFilter.FILTER_ID, match.getType());
		assertEquals("accenture", match.getValue());
		assertTrue(match.getMessage().contains("44"));
	}

	@Test
	public void simpleMatchOnAddress2() {
		final List<Match> result = companyFilter("Accenture", null).match(bundleAddress2("accenture"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(CompanyFilter.FILTER_ID, match.getType());
		assertEquals("accenture", match.getValue());
		assertTrue(match.getMessage().contains("44"));
	}

	@Test
	public void simpleMatchOnAddress3() {
		final List<Match> result = companyFilter("Accenture", null).match(bundleAddress3("accenture"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(CompanyFilter.FILTER_ID, match.getType());
		assertEquals("accenture", match.getValue());
		assertTrue(match.getMessage().contains("44"));
	}

	@Test
	public void simpleMatchOnName() {
		final List<Match> result = companyFilter("Accenture", null).match(bundleLastname("accenture"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(CompanyFilter.FILTER_ID, match.getType());
		assertEquals("accenture", match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchOnNameWithNumber() {
		assertTrue(companyFilter("good2go", null).match(bundleLastname("good go")).size() <= 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("good1go")).size() <= 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("good23go")).size() <= 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("2good2go")).size() <= 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("good2go2")).size() <= 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("Agood2go")).size() <= 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("good2goA")).size() <= 0);

		assertTrue(companyFilter("good2go", null).match(bundleLastname("good2go")).size() > 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("a good2go b")).size() > 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("good2go b")).size() > 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("a good2go")).size() > 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("3 good2go")).size() > 0);
		assertTrue(companyFilter("good2go", null).match(bundleLastname("good2go 3")).size() > 0);

		assertTrue(companyFilter("2 good go", null).match(bundleLastname("good2go")).size() <= 0);
		assertTrue(companyFilter("good go 2", null).match(bundleLastname("good2go")).size() <= 0);
		assertTrue(companyFilter("good 2 go", null).match(bundleLastname("good2go")).size() <= 0);

		assertTrue(companyFilter("2 good go", null).match(bundleLastname("good 2 go")).size() > 0);
		assertTrue(companyFilter("good go 2", null).match(bundleLastname("good 2 go")).size() > 0);
		assertTrue(companyFilter("good 2 go", null).match(bundleLastname("good 2 go")).size() > 0);

		assertTrue(companyFilter("2 good go", null).match(bundleLastname("a good 2 go b")).size() > 0);
		assertTrue(companyFilter("good go 2", null).match(bundleLastname("a good 2 go b")).size() > 0);
		assertTrue(companyFilter("good 2 go", null).match(bundleLastname("a good 2 go b")).size() > 0);

		assertTrue(companyFilter("good go", null).match(bundleLastname("good2go")).size() > 0);
		assertTrue(companyFilter("good go", null).match(bundleLastname("1good2go3")).size() > 0);
		assertTrue(companyFilter("good go", null).match(bundleLastname("2 good go")).size() > 0);
		assertTrue(companyFilter("good go", null).match(bundleLastname("good go 2")).size() > 0);
	}

	@Test
	public void simpleNoMatch() {
		final List<Match> result = companyFilter("doesNotMatch", null).match(bundle());
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void matchMultipleWords() {
		assertTrue(companyFilter("Sabine Sabine", null).match(bundle()).size() > 0);
		assertTrue(companyFilter("Sabine Fischer", null).match(bundle()).size() > 0);
		assertTrue(companyFilter("Sabine sabsi", null).match(bundle()).size() > 0);
		assertTrue(companyFilter("fischer sabine", null).match(bundle()).size() > 0);
		assertTrue(companyFilter("sabfisch Sabine fischer sabsi", null).match(bundle()).size() > 0);
	}

	@Test
	public void dontMatchMultipleWords() {
		assertEquals(0, companyFilter("Sabine NoMatch Fischer", null).match(bundle()).size());
		assertEquals(0, companyFilter("Sabine sabsi NoMatch", null).match(bundle()).size());
		assertEquals(0, companyFilter("NoMatch fischer sabine", null).match(bundle()).size());
		assertEquals(0, companyFilter("sabfisch Sabine fischer sabsi NoMatch", null).match(bundle()).size());
	}

	@Test
	public void matchNameSpecialCharacters() {
		final Filter filter = companyFilter("mueller", null);
		assertTrue(filter.match(bundleLastname("mueller")).size() > 0);
		assertTrue(filter.match(bundleLastname(" mueller ")).size() > 0);
		assertTrue(filter.match(bundleLastname(" \t mueller\t\t ")).size() > 0);
		assertTrue(filter.match(bundleLastname("!mueller-")).size() > 0);
		assertTrue(filter.match(bundleLastname("@mueller?")).size() > 0);
		assertTrue(filter.match(bundleLastname("-mueller=")).size() > 0);
		assertTrue(filter.match(bundleLastname("()|mueller[]{}")).size() > 0);
		assertTrue(filter.match(bundleLastname(" =2= mueller ")).size() > 0);
		assertTrue(filter.match(bundleLastname("1mueller8")).size() > 0);
		assertTrue(filter.match(bundleLastname("mueller1987")).size() > 0);
	}

	@Test
	public void realUseCases() {
		assertFilterMatches("V U T", "V.U.T. Verein");
		assertFilterNotMatches("V U T", "VUT Verein");
		assertFilterMatches("VUT", "VUT Verein");
		assertFilterMatches("Sasse Partner", "Sasse & Partner");
		assertFilterMatches("Undercover Records", "Undercover-Records");
		assertFilterMatches("E A M Records", "E.A.M. Records");
		assertFilterMatches("St Martin Studios", "St. Martin's Studios");
		assertFilterMatches("A2B services", "A2B-services");
		assertFilterMatches("amazon", "Amazon.de GmbH");
	}

	private void assertFilterMatches(String filterName, String lastname) {
		assertTrue(companyFilter(filterName, null).match(bundleLastname(lastname)).size() > 0);
	}

	private void assertFilterNotMatches(String filterName, String lastname) {
		assertTrue(companyFilter(filterName, null).match(bundleLastname(lastname)).size() <= 0);
	}

	@Test
	public void matchNameWithAccent() {
		final Filter filter = companyFilter("probó", null);
		assertTrue(filter.match(bundleLastname("probó")).size() > 0);
		assertTrue(filter.match(bundleLastname("!probó@")).size() > 0);
	}

	@Test
	public void matchNameUmlaute() {
		Filter filter = companyFilter("müller", null);
		assertTrue(filter.match(bundleLastname("müller")).size() > 0);
		assertTrue(filter.match(bundleLastname("___müller?1")).size() > 0);

		filter = companyFilter("cät", null);
		assertTrue(filter.match(bundleLastname("cät")).size() > 0);
		assertTrue(filter.match(bundleLastname("caet")).size() > 0);

		filter = companyFilter("cüt", null);
		assertTrue(filter.match(bundleLastname("cüt")).size() > 0);
		assertTrue(filter.match(bundleLastname("cuet")).size() > 0);

		filter = companyFilter("höt", null);
		assertTrue(filter.match(bundleLastname("höt")).size() > 0);
		assertTrue(filter.match(bundleLastname("hoet")).size() > 0);
	}

	@Test
	public void matchOrderWithUmlaute() {
		Filter filter = companyFilter("mueller", null);
		assertTrue(filter.match(bundleLastname("müller")).size() > 0);

		filter = companyFilter("caet", null);
		assertTrue(filter.match(bundleLastname("cät")).size() > 0);

		filter = companyFilter("cuet", null);
		assertTrue(filter.match(bundleLastname("cüt")).size() > 0);

		filter = companyFilter("hoet", null);
		assertTrue(filter.match(bundleLastname("höt")).size() > 0);
	}

	@Test
	public void matchNameUnicode() {
		final Filter filter = companyFilter("高原文彦", null);
		assertTrue(filter.match(bundleLastname("高原文彦")).size() > 0);
		assertTrue(filter.match(bundleLastname("!2@-高原文彦")).size() > 0);
	}

	@Test
	public void matchNameWithCountry() {
		assertTrue(companyFilter("sabine", "DE").match(bundle()).size() > 0);
	}

	@Test
	public void dontMatchNameWithCountry() {
		assertEquals(0, companyFilter("sabine", "UY").match(bundle()).size());
		assertEquals(0, companyFilter("sabine", "AR").match(bundle()).size());
	}

	@Test
	public void matchAllFields() {

		// user
		assertTrue(companyFilter("sabine").match(bundle()).size() > 0); // firstname
		assertTrue(companyFilter("fischer").match(bundle()).size() > 0); // lastname
		assertTrue(companyFilter("sabfisch").match(bundle()).size() > 0); // username
		assertTrue(companyFilter("sabsi").match(bundle()).size() > 0); // mail

		// address
		assertTrue(companyFilter("julia").match(bundle()).size() > 0); // firstname
		assertTrue(companyFilter("mueller").match(bundle()).size() > 0); // lastname
		assertTrue(companyFilter("safi").match(bundle()).size() > 0); // company
		assertTrue(companyFilter("studansky").match(bundle()).size() > 0); // address1
		assertTrue(companyFilter("stock").match(bundle()).size() > 0); // address2
		assertTrue(companyFilter("tuer").match(bundle()).size() > 0); // address3

	}

	@Test
	public void noMatchFields() {
		// user
		assertTrue(companyFilter("Wien").match(bundle()).size() == 0); // city
		assertTrue(companyFilter("DE").match(bundle()).size() == 0); // country
	}

	@Test
	public void simpleNoMatchPlatformAccountId() {
		final List<Match> result = companyFilter("Accenture").match(bundlePlatform("amazon.us@dodax.com"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simplePartialMatchPlatformAccountId() {
		final List<Match> result = companyFilter("Accenture").match(bundlePlatform("amazon.uk"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	public void simpleMatchPlatformAccountIdWithDifferentCase() {
		final List<Match> result = companyFilter("Accenture").match(bundlePlatform(platformAccountId.toUpperCase()));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match companyMatch = result.get(0);
		assertEquals(CompanyFilter.FILTER_ID, companyMatch.getType());
		assertTrue("accenture".equals(companyMatch.getValue()));
		assertTrue(companyMatch.getMessage().contains("55"));
	}

	@Test
	public void simpleMatchOnNameAndPlatformAccountId() {
		final List<Match> result = companyFilter("Accenture", null).match(bundlePlatform(platformAccountId));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(CompanyFilter.FILTER_ID, match.getType());
		assertEquals("accenture", match.getValue());
		assertTrue(match.getMessage().contains("55"));
	}

	@Test
	public void notMatchGeneratedAmazonMail() {
		final String email = "1ktkjrc1nl0t9fh@marketplace.amazon.com";
		assertTrue(companyFilter("amazon").match(bundleMail(email)).size() <= 0);
		assertTrue(companyFilter("marketplace").match(bundleMail(email)).size() <= 0);
		assertTrue(companyFilter("com").match(bundleMail(email)).size() <= 0);
		assertTrue(companyFilter("fh").match(bundleMail(email)).size() <= 0);
		assertTrue(companyFilter("nl").match(bundleMail(email)).size() <= 0);
		assertTrue(companyFilter("ktkjrc").match(bundleMail(email)).size() <= 0);
		assertTrue(companyFilter("1ktkjrc1nl0t9fh").match(bundleMail(email)).size() <= 0);

		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.co.uk")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.fr")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.ca")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.de")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.com")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.co.jp")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@m.marketplace.amazon.co.jp")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.it")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.es")).size() <= 0);
		assertTrue(companyFilter("amazon").match(bundleMail("l@marketplace.amazon.com.mx")).size() <= 0);
	}

	@Test
	public void matchMailAddress() {
		assertTrue(companyFilter("latop").match(bundleMail("sven@latop.at")).size() > 0);
		assertTrue(companyFilter("sven").match(bundleMail("sven@latop.at")).size() > 0);
	}

	// helpers

	private Filter companyFilter(String name) {
		return companyFilter(name, null);
	}

	private Filter companyFilter(String name, String country) {
		return filterRepository.saveCompanyFilter(name, country, null, null, platformAccountGroupIds);
	}

	private Filter companyFilter(String name, String country, String zip) {
		return filterRepository.saveCompanyFilter(name, country, zip, null, platformAccountGroupIds);
	}

	private Bundle bundle() {
		return customBundle(null, null, null, null, null, null, null);
	}

	private Bundle bundleMail(String email) {
		return customBundle(null, null, null, null, null, null, email);
	}

	private Bundle bundlePlatform(String platformAccount) {
		return customBundle(null, "accenture", null, null, null, platformAccount, null);
	}

	private Bundle bundleLastname(String lastname) {
		return customBundle(null, lastname, null, null, null, null, null);
	}

	private Bundle bundleAddress1(String address1) {
		return customBundle(null, null, address1, null, null, null, null);
	}

	private Bundle bundleAddress2(String address2) {
		return customBundle(null, null, null, address2, null, null, null);
	}

	private Bundle bundleAddress3(String address3) {
		return customBundle(null, null, null, null, address3, null, null);
	}

	@Test
	public void matchWithinMaxDistance() {
		final List<Match> result = companyFilter("Accenture", "de", "1020").match(bundleAddress1("accenture"));
		assertNotNull(result);
		assertEquals(1, result.size());
		final Match match = result.get(0);
		assertEquals(CompanyFilter.FILTER_ID, match.getType());
		assertEquals("accenture", match.getValue());
		assertTrue(match.getMessage().contains("44"));
	}

	@Test
	public void matchOutsideMaxDistance() {
		final List<Match> result = companyFilter("Accenture", "de", "9992").match(bundleAddress1("accenture"));
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	private Bundle customBundle(String firstname, String lastname, String address1, String address2, String address3,
			String plaformAccountId, String mail) {
		if (firstname == null)
			firstname = "Sabine";
		if (lastname == null)
			lastname = "Fischer";
		if (address1 == null)
			address1 = "U Studansky";
		if (address2 == null)
			address2 = "Stock 8";
		if (address3 == null)
			address3 = "Tuer 4";
		if (mail == null)
			mail = "sabsi@fischer.at";

		return orderRepository.newBundle(orderRepository.newUser(55L, firstname, lastname, "sabfisch", mail),
				Arrays.asList(orderRepository.newAddress(44L, "Julia", "Mueller", "SaFi GmbH", address1, address2,
						address3, "1010", "Wien", "DE", "+43")),
				plaformAccountId);
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

