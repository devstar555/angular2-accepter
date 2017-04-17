package model.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.zipcode.TestZipCodeCreator;

public abstract class FilterRepositoryTest {

	protected abstract FilterRepository newRepository();

	private FilterRepository repository;

	@Before
	public void beforeRepositoryTest() {
		this.repository = newRepository();
	}

	@After
	public void afterRepositoryTest() {
		this.repository = null;
	}

	@Test
	public void saveAndRead_personFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPersonFilters().size());
		final PersonFilter saved = repository.savePersonFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllPersonFilters().size());

		assertNotNull(saved.getId());
		assertEquals("fischer", saved.getName());
		assertEquals("DE", saved.getCountry());
		assertEquals("1010", saved.getZip());
		assertEquals("desc", saved.getDescription());
		assertEquals(saved.getId(), repository.findAllFilters().get(0).getId());
		assertEquals(saved.getId(), repository.findAllPersonFilters().get(0).getId());

		PersonFilter filter = repository.findPersonFilter(saved.getId());

		assertNotNull(filter);
		assertEquals(saved.getId(), filter.getId());
		assertEquals("fischer", filter.getName());
		assertEquals("DE", filter.getCountry());
		assertEquals("1010", filter.getZip());
		assertEquals("desc", filter.getDescription());
	}

	@Test
	public void updateAndRead_personFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPersonFilters().size());
		repository.savePersonFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.savePersonFilter("mendelez", "de", "1020", "desc", getPlatformAccountGroupIds());
		repository.savePersonFilter("suarez", "de", "1030", "desc", getPlatformAccountGroupIds());
		final PersonFilter saved = repository.savePersonFilter("fischer", "de", "1040", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllPersonFilters().size());

		final PersonFilter updated = repository.updatePersonFilter(saved.getId(), "name updated", "de", "1050",
				"description updated", getPlatformAccountGroupIds());
		assertEquals("name updated", updated.getName());
		assertEquals("DE", updated.getCountry());
		assertEquals("1050", updated.getZip());
		assertEquals("description updated", updated.getDescription());

		PersonFilter filter = repository.findPersonFilter(updated.getId());

		assertNotNull(filter);
		assertEquals(updated.getId(), filter.getId());
		assertEquals("name updated", filter.getName());
		assertEquals("DE", filter.getCountry());
		assertEquals("1050", filter.getZip());
		assertEquals("description updated", filter.getDescription());
	}

	@Test
	public void delete_personFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPersonFilters().size());
		repository.savePersonFilter("tylor", "de", null, "desc", getPlatformAccountGroupIds());
		repository.savePersonFilter("mendelez", "de", null, "desc", getPlatformAccountGroupIds());
		repository.savePersonFilter("suarez", "de", "1010", "desc", getPlatformAccountGroupIds());
		final PersonFilter saved = repository.savePersonFilter("fischer", "de", "1020", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllPersonFilters().size());

		repository.deletePersonFilter(saved.getId());
		refreshCaches();
		assertEquals(3, repository.findAllFilters().size());
		assertEquals(3, repository.findAllPersonFilters().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoName_personFilter() {

		final PersonFilter saved = repository.savePersonFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		repository.updatePersonFilter(saved.getId(), "", "de", "1020", "description updated",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithInvalidCountry_personFilter() {

		final PersonFilter saved = repository.savePersonFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		repository.updatePersonFilter(saved.getId(), "", "ZZ", "1020", "description updated",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithInvalidZip_personFilter() {

		final PersonFilter saved = repository.savePersonFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		repository.updatePersonFilter(saved.getId(), "fischer", "de", "123456", "description updated",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoId_personFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPersonFilters().size());
		repository.savePersonFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.savePersonFilter("mendelez", "de", "1020", "desc", getPlatformAccountGroupIds());
		repository.savePersonFilter("suarez", "de", "1030", "desc", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(3, repository.findAllFilters().size());
		assertEquals(3, repository.findAllPersonFilters().size());

		repository.updatePersonFilter(null, "name updated", "de", "1040", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteOtherTypeOfFilter_personFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPersonFilters().size());
		CompanyFilter saved = repository.saveCompanyFilter("google", "de", null, "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());

		repository.deletePersonFilter(saved.getId());

	}

	@Test(expected = IllegalArgumentException.class)
	public void modifyOtherTypeOfFilter_personFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPersonFilters().size());
		CompanyFilter saved = repository.saveCompanyFilter("google", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());

		repository.updatePersonFilter(saved.getId(), "new name", "LI", "1020", "new desc",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void deletePersonFilter_nullId() {
		repository.deletePersonFilter(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_nullName() {
		repository.savePersonFilter(null, "de", "1010", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_blankName() {
		repository.savePersonFilter("   \t ", "de", "1020", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_invalidCountry() {
		repository.savePersonFilter("john peterson", "ZZ", "1020", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_cleanZipWithoutCountry() {
		repository.savePersonFilter("john peterson", "", "1010", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_invalidZip1() {
		repository.savePersonFilter("john peterson", "de", "222222", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_invalidZip2() {
		repository.savePersonFilter("john peterson", "de", "222222", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_invalidZip3() {
		repository.savePersonFilter("john peterson", "de", "#$%^&", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_invalidZip4() {
		repository.savePersonFilter("john peterson", "de", "a1b2c", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_emptyName() {
		repository.savePersonFilter("", "de", "1010", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePersonFilter_nonAlphabetic() {
		repository.savePersonFilter("!asdf", "de", "1010", "desc", getPlatformAccountGroupIds());
	}

	@Test
	public void savePersonFilter_unicode() {
		assertEquals("高原文彦",
				repository.savePersonFilter("高原文彦", "de", "1010", "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void savePersonFilter_umlaute() {
		assertEquals("müller", repository
				.savePersonFilter("müller", "de", "1010", "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void savePersonFilter_cleanName() {
		assertEquals("fischer", repository
				.savePersonFilter("  fischer \t", null, null, "desc", getPlatformAccountGroupIds()).getName());
		assertEquals("fischer", repository
				.savePersonFilter(" FISchER  ", null, null, "desc", getPlatformAccountGroupIds()).getName());
		assertEquals("sabine fischer", repository
				.savePersonFilter(" SabiNE \t FISchER  ", null, null, "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void savePersonFilter_cleanDescription() {
		assertEquals(null,
				repository.savePersonFilter("n", null, null, null, getPlatformAccountGroupIds()).getDescription());
		assertEquals(null,
				repository.savePersonFilter("n", null, null, "", getPlatformAccountGroupIds()).getDescription());
		assertEquals(null, repository.savePersonFilter("n", null, null, " \t   ", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a",
				repository.savePersonFilter("n", null, null, "a", getPlatformAccountGroupIds()).getDescription());
		assertEquals("a", repository.savePersonFilter("n", null, null, "   \ta", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a", repository.savePersonFilter("n", null, null, "a\t   ", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a", repository
				.savePersonFilter("n", null, null, "  \t a  \t\t", getPlatformAccountGroupIds()).getDescription());
		assertEquals("a  b", repository
				.savePersonFilter("n", null, null, "  \t a  b  ", getPlatformAccountGroupIds()).getDescription());
	}

	@Test
	public void savePersonFilter_cleanCountry() {
		assertEquals("DE", repository.savePersonFilter("f", "  de \t ", null, "desc", getPlatformAccountGroupIds())
				.getCountry());
		assertEquals("DE",
				repository.savePersonFilter("f", "De", null, "desc", getPlatformAccountGroupIds()).getCountry());
		assertEquals(null, repository.savePersonFilter("f", "   \t ", null, "desc", getPlatformAccountGroupIds())
				.getCountry());
		assertEquals(null,
				repository.savePersonFilter("f", "", null, "desc", getPlatformAccountGroupIds()).getCountry());
		assertEquals(null,
				repository.savePersonFilter("f", null, null, "desc", getPlatformAccountGroupIds()).getCountry());
	}

	@Test
	public void savePersonFilter_cleanZip() {

		assertEquals("1010",
				repository.savePersonFilter("f", "de", "1010", "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals(null,
				repository.savePersonFilter("f", "de", null, "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals(null,
				repository.savePersonFilter("f", "", null, "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals(null,
				repository.savePersonFilter("f", null, null, "desc", getPlatformAccountGroupIds()).getZip());
	}

	// companies tests

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterInvalidCountry() {
		repository.saveCompanyFilter("the company", "XX", null, "the description", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilter_cleanZipWithoutCountry() {
		repository.saveCompanyFilter("the company", "", "1010", "the description", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterInvalidZip1() {
		repository.saveCompanyFilter("the company", "de", "123456", "the description",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterInvalidZip2() {
		repository.saveCompanyFilter("the company", "de", "abcde", "the description",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterInvalidZip3() {
		repository.saveCompanyFilter("the company", "de", "a1234", "the description",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterInvalidZip4() {
		repository.saveCompanyFilter("the company", "de", "$#%&*", "the description",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterNullName() {
		repository.saveCompanyFilter(null, "CZ", null, "the description", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterEmptyName() {
		repository.saveCompanyFilter("", "CZ", null, "the description", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterInvalidName() {
		repository.saveCompanyFilter("1invalid$", "CZ", null, "the description", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveCompanyFilterInvalidNameMultipleWords() {
		repository.saveCompanyFilter("valid invalid%$%", "CZ", null, "the description",
				getPlatformAccountGroupIds());
	}

	@Test
	public void saveAndReadCompanyFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllCompanyFilters().size());
		final CompanyFilter saved = repository.saveCompanyFilter("accenture", "de", "1010", "the description",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllCompanyFilters().size());

		assertNotNull(saved.getId());
		assertEquals("accenture", saved.getName());
		assertEquals("DE", saved.getCountry());
		assertEquals("1010", saved.getZip());
		assertEquals("the description", saved.getDescription());
		assertEquals(saved.getId(), repository.findAllFilters().get(0).getId());
		assertEquals(saved.getId(), repository.findAllCompanyFilters().get(0).getId());

		CompanyFilter filter = repository.findCompanyFilter(saved.getId());

		assertNotNull(filter);
		assertEquals(saved.getId(), filter.getId());
		assertEquals("accenture", filter.getName());
		assertEquals("DE", filter.getCountry());
		assertEquals("1010", filter.getZip());
		assertEquals("the description", filter.getDescription());
	}

	@Test
	public void saveCompanyFilter_unicode() {
		assertEquals("高原文彦", repository
				.saveCompanyFilter("高原文彦", "de", "1010", "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void saveCompanyFilter_umlaute() {
		assertEquals("müller", repository
				.saveCompanyFilter("müller", "de", "1010", "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void saveCompanyFilter_cleanName() {
		assertEquals("accenture", repository
				.saveCompanyFilter("  accenture \t", null, null, "desc", getPlatformAccountGroupIds()).getName());
		assertEquals("accenture", repository
				.saveCompanyFilter(" AcCeNtURe  ", null, null, "desc", getPlatformAccountGroupIds()).getName());
		assertEquals("coca cola", repository
				.saveCompanyFilter(" COCa \t\t\t colA  ", null, null, "desc", getPlatformAccountGroupIds()).getName());
		assertEquals("good 2 go", repository
				.saveCompanyFilter("good 2 go", null, null, "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void saveCompanyFilter_cleanDescription() {
		assertEquals(null, repository.saveCompanyFilter("n", null, null, null, getPlatformAccountGroupIds())
				.getDescription());
		assertEquals(null,
				repository.saveCompanyFilter("n", null, null, "", getPlatformAccountGroupIds()).getDescription());
		assertEquals(null, repository.saveCompanyFilter("n", null, null, " \t   ", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a",
				repository.saveCompanyFilter("n", null, null, "a", getPlatformAccountGroupIds()).getDescription());
		assertEquals("a", repository.saveCompanyFilter("n", null, null, "   \ta", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a", repository.saveCompanyFilter("n", null, null, "a\t   ", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a", repository
				.saveCompanyFilter("n", null, null, "  \t a  \t\t", getPlatformAccountGroupIds()).getDescription());
		assertEquals("a  b", repository
				.saveCompanyFilter("n", null, null, "  \t a  b  ", getPlatformAccountGroupIds()).getDescription());
	}

	@Test
	public void saveCompanyFilter_cleanCountry() {
		assertEquals("CZ", repository
				.saveCompanyFilter("f", "  cz \t ", null, "desc", getPlatformAccountGroupIds()).getCountry());
		assertEquals("CZ",
				repository.saveCompanyFilter("f", "cZ", null, "desc", getPlatformAccountGroupIds()).getCountry());
		assertEquals(null, repository.saveCompanyFilter("f", "   \t ", null, "desc", getPlatformAccountGroupIds())
				.getCountry());
		assertEquals(null,
				repository.saveCompanyFilter("f", "", null, "desc", getPlatformAccountGroupIds()).getCountry());
		assertEquals(null,
				repository.saveCompanyFilter("f", null, null, "desc", getPlatformAccountGroupIds()).getCountry());
	}

	@Test
	public void updateAndRead_companyFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllCompanyFilters().size());
		repository.saveCompanyFilter("google", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("facebook", "de", "1020", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("ibm", "de", "1030", "desc", getPlatformAccountGroupIds());
		final CompanyFilter saved = repository.saveCompanyFilter("dell", "de", "1040", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllCompanyFilters().size());

		CompanyFilter updated = repository.updateCompanyFilter(saved.getId(), "name updated with 2", "de", "1050",
				"description updated", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals("name updated with 2", updated.getName());
		assertEquals("DE", updated.getCountry());
		assertEquals("1050", updated.getZip());
		assertEquals("description updated", updated.getDescription());

		CompanyFilter filter = repository.findCompanyFilter(updated.getId());

		assertNotNull(filter);
		assertEquals(updated.getId(), filter.getId());
		assertEquals("name updated with 2", filter.getName());
		assertEquals("DE", filter.getCountry());
		assertEquals("1050", filter.getZip());
		assertEquals("description updated", filter.getDescription());
	}

	@Test
	public void delete_companyFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllCompanyFilters().size());
		repository.saveCompanyFilter("google", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("facebook", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("ibm", "de", "1010", "desc", getPlatformAccountGroupIds());
		final CompanyFilter saved = repository.saveCompanyFilter("dell", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllCompanyFilters().size());

		repository.deleteCompanyFilter(saved.getId());
		refreshCaches();
		assertEquals(3, repository.findAllFilters().size());
		assertEquals(3, repository.findAllCompanyFilters().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoName_companyFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllCompanyFilters().size());
		repository.saveCompanyFilter("google", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("facebook", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("ibm", "de", "1010", "desc", getPlatformAccountGroupIds());
		final CompanyFilter saved = repository.saveCompanyFilter("dell", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllCompanyFilters().size());

		repository.updateCompanyFilter(saved.getId(), "", "de", "1010", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithInvalidCountry_companyFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllCompanyFilters().size());
		repository.saveCompanyFilter("google", "de", "11111", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("facebook", "de", "11111", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("ibm", "de", "11111", "desc", getPlatformAccountGroupIds());
		final CompanyFilter saved = repository.saveCompanyFilter("dell", "de", "11111", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllCompanyFilters().size());

		repository.updateCompanyFilter(saved.getId(), "new name", "ZZ", "88888", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithInvalidZip_companyFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllCompanyFilters().size());
		repository.saveCompanyFilter("google", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("facebook", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("ibm", "de", "1010", "desc", getPlatformAccountGroupIds());
		final CompanyFilter saved = repository.saveCompanyFilter("dell", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllCompanyFilters().size());

		repository.updateCompanyFilter(saved.getId(), "new name", "us", "888888", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoId_companyFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllCompanyFilters().size());
		repository.saveCompanyFilter("google", "de", "11111", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("facebook", "de", "11111", "desc", getPlatformAccountGroupIds());
		repository.saveCompanyFilter("ibm", "de", "11111", "desc", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(3, repository.findAllFilters().size());
		assertEquals(3, repository.findAllCompanyFilters().size());

		repository.updateCompanyFilter(null, "name updated", "de", "1010", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void deleteCompanyFilter_nullId() {
		repository.deleteCompanyFilter(null);
	}

	// street filter tests

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterInvalidCountry() {
		repository.saveStreetFilter("boulevard spain", "YY", "1010", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilter_cleanZipWithoutCountry() {
		repository.saveStreetFilter("boulevard spain", "", "1010", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterInvalidZip1() {
		repository.saveStreetFilter("boulevard spain", "de", "111111", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterInvalidZip2() {
		repository.saveStreetFilter("boulevard spain", "de", "abcde", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterInvalidZip3() {
		repository.saveStreetFilter("boulevard spain", "de", "#$%^&", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterInvalidZip4() {
		repository.saveStreetFilter("boulevard spain", "de", "a1b2c", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterNullName() {
		repository.saveStreetFilter(null, "de", "1010", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterEmptyName() {
		repository.saveStreetFilter("", "de", "1010", "the description", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterInvalidName() {
		repository.saveStreetFilter("invalid$", "de", "1010", "the description", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveStreetFilterInvalidNameMultipleWords() {
		repository.saveStreetFilter("valid _invalid", "de", "1010", "the description",
				getPlatformAccountGroupIds());
	}

	@Test
	public void saveAndReadStreetFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllStreetFilters().size());
		final StreetFilter saved = repository.saveStreetFilter("Boulevard Spain", "de", "1010", "the description",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllStreetFilters().size());

		assertNotNull(saved.getId());
		assertEquals("boulevard spain", saved.getName());
		assertEquals("DE", saved.getCountry());
		assertEquals("1010", saved.getZip());
		assertEquals("the description", saved.getDescription());
		assertEquals(saved.getId(), repository.findAllFilters().get(0).getId());
		assertEquals(saved.getId(), repository.findAllStreetFilters().get(0).getId());

		StreetFilter filter = repository.findStreetFilter(saved.getId());

		assertNotNull(filter);
		assertEquals(saved.getId(), filter.getId());
		assertEquals("boulevard spain", filter.getName());
		assertEquals("DE", filter.getCountry());
		assertEquals("1010", filter.getZip());
		assertEquals("the description", filter.getDescription());
	}

	@Test
	public void saveStreetFilter_unicode() {
		assertEquals("高原文彦",
				repository.saveStreetFilter("高原文彦", "de", "1010", "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void saveAndUpdateStreetFilter_number() {
		StreetFilter filter = repository.saveStreetFilter("Str 17", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		assertEquals("str 17", filter.getName());
		filter = repository.updateStreetFilter(filter.getId(), "A 18", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		assertEquals("a 18", filter.getName());
	}

	@Test
	public void saveStreetFilter_umlaute() {
		assertEquals("müller", repository
				.saveStreetFilter("müller", "de", "1010", "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void saveStreetFilter_cleanName() {
		assertEquals("accenture", repository
				.saveStreetFilter("  accenture \t", null, null, "desc", getPlatformAccountGroupIds()).getName());
		assertEquals("accenture", repository
				.saveStreetFilter(" AcCeNtURe  ", null, null, "desc", getPlatformAccountGroupIds()).getName());
		assertEquals("coca cola", repository
				.saveStreetFilter(" COCa \t\t\t colA  ", null, null, "desc", getPlatformAccountGroupIds()).getName());
	}

	@Test
	public void saveStreetFilter_cleanDescription() {
		assertEquals(null,
				repository.saveStreetFilter("n", null, null, null, getPlatformAccountGroupIds()).getDescription());
		assertEquals(null,
				repository.saveStreetFilter("n", null, null, "", getPlatformAccountGroupIds()).getDescription());
		assertEquals(null, repository.saveStreetFilter("n", null, null, " \t   ", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a",
				repository.saveStreetFilter("n", null, null, "a", getPlatformAccountGroupIds()).getDescription());
		assertEquals("a", repository.saveStreetFilter("n", null, null, "   \ta", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a", repository.saveStreetFilter("n", null, null, "a\t   ", getPlatformAccountGroupIds())
				.getDescription());
		assertEquals("a", repository
				.saveStreetFilter("n", null, null, "  \t a  \t\t", getPlatformAccountGroupIds()).getDescription());
		assertEquals("a  b", repository
				.saveStreetFilter("n", null, null, "  \t a  b  ", getPlatformAccountGroupIds()).getDescription());
	}

	@Test
	public void saveStreetFilter_cleanCountry() {
		assertEquals("CZ", repository.saveStreetFilter("f", "  cz \t ", null, "desc", getPlatformAccountGroupIds())
				.getCountry());
		assertEquals("CZ",
				repository.saveStreetFilter("f", "cZ", null, "desc", getPlatformAccountGroupIds()).getCountry());
		assertEquals(null, repository.saveStreetFilter("f", "   \t ", null, "desc", getPlatformAccountGroupIds())
				.getCountry());
		assertEquals(null,
				repository.saveStreetFilter("f", "", null, "desc", getPlatformAccountGroupIds()).getCountry());
		assertEquals(null,
				repository.saveStreetFilter("f", null, null, "desc", getPlatformAccountGroupIds()).getCountry());
	}

	@Test
	public void saveStreetFilter_cleanZip() {
		assertEquals("1010",
				repository.saveStreetFilter("f", "de", "1010", "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals("1020",
				repository.saveStreetFilter("f", "de", "1020", "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals("1030",
				repository.saveStreetFilter("f", "de", "1030", "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals("1040",
				repository.saveStreetFilter("f", "de", "1040", "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals("1050",
				repository.saveStreetFilter("f", "de", "1050", "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals(null,
				repository.saveStreetFilter("f", "", "", "desc", getPlatformAccountGroupIds()).getZip());
		assertEquals(null,
				repository.saveStreetFilter("f", "", null, "desc", getPlatformAccountGroupIds()).getZip());
	}

	@Test
	public void updateAndRead_streetFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllStreetFilters().size());
		repository.saveStreetFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("mendelez", "de", "1020", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("suarez", "de", "1030", "desc", getPlatformAccountGroupIds());
		final StreetFilter saved = repository.saveStreetFilter("fischer", "de", "1040", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllStreetFilters().size());

		final StreetFilter updated = repository.updateStreetFilter(saved.getId(), "name updated", "de", "1050",
				"description updated", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals("name updated", updated.getName());
		assertEquals("DE", updated.getCountry());
		assertEquals("1050", updated.getZip());
		assertEquals("description updated", updated.getDescription());

		StreetFilter filter = repository.findStreetFilter(updated.getId());

		assertNotNull(filter);
		assertEquals(updated.getId(), filter.getId());
		assertEquals("name updated", filter.getName());
		assertEquals("DE", filter.getCountry());
		assertEquals("1050", filter.getZip());
		assertEquals("description updated", filter.getDescription());
	}

	@Test
	public void delete_streetFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllStreetFilters().size());
		repository.saveStreetFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("mendelez", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("suarez", "de", "1010", "desc", getPlatformAccountGroupIds());
		final StreetFilter saved = repository.saveStreetFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllStreetFilters().size());

		repository.deleteStreetFilter(saved.getId());
		refreshCaches();
		assertEquals(3, repository.findAllFilters().size());
		assertEquals(3, repository.findAllStreetFilters().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoName_streetFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllStreetFilters().size());
		repository.saveStreetFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("mendelez", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("suarez", "de", "1010", "desc", getPlatformAccountGroupIds());
		final StreetFilter saved = repository.saveStreetFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllStreetFilters().size());

		repository.updateStreetFilter(saved.getId(), "", "DE", "1010", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoId_streetFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllStreetFilters().size());
		repository.saveStreetFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("mendelez", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("suarez", "de", "1010", "desc", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(3, repository.findAllFilters().size());
		assertEquals(3, repository.findAllStreetFilters().size());

		repository.updateStreetFilter(null, "name updated", "DE", "1010", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithInvalidCountry_streetFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllStreetFilters().size());
		repository.saveStreetFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("mendelez", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("suarez", "de", "1010", "desc", getPlatformAccountGroupIds());
		final StreetFilter saved = repository.saveStreetFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllStreetFilters().size());
		repository.updateStreetFilter(saved.getId(), "new name", "INVALID COUNTRY", "1010", "description updated",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithInvalidZip_streetFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllStreetFilters().size());
		repository.saveStreetFilter("tylor", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("mendelez", "de", "1010", "desc", getPlatformAccountGroupIds());
		repository.saveStreetFilter("suarez", "de", "1010", "desc", getPlatformAccountGroupIds());
		final StreetFilter saved = repository.saveStreetFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(4, repository.findAllFilters().size());
		assertEquals(4, repository.findAllStreetFilters().size());
		repository.updateStreetFilter(saved.getId(), "new name", "de", "123456", "description updated",
				getPlatformAccountGroupIds());
	}

	// email filter tests

	@Test
	public void saveAndRead_emailFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllEmailFilters().size());
		final EmailFilter saved = repository.saveEmailFilter("johnstockton@gmail.com", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllEmailFilters().size());

		assertNotNull(saved.getId());
		assertEquals("johnstockton@gmail.com", saved.getName());
		assertEquals("desc", saved.getDescription());
		assertEquals(saved.getId(), repository.findAllFilters().get(0).getId());
		assertEquals(saved.getId(), repository.findAllEmailFilters().get(0).getId());

		EmailFilter filter = repository.findEmailFilter(saved.getId());

		assertNotNull(filter);
		assertEquals(saved.getId(), filter.getId());
		assertEquals("johnstockton@gmail.com", filter.getName());
		assertEquals("desc", filter.getDescription());
	}

	@Test
	public void updateAndRead_emailFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllEmailFilters().size());

		final EmailFilter saved = repository.saveEmailFilter("john@google.com", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllEmailFilters().size());

		final EmailFilter updated = repository.updateEmailFilter(saved.getId(), "updated@google.com",
				"description updated", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals("updated@google.com", updated.getName());
		assertEquals("description updated", updated.getDescription());

		EmailFilter filter = repository.findEmailFilter(updated.getId());

		assertNotNull(filter);
		assertEquals(updated.getId(), filter.getId());
		assertEquals("updated@google.com", filter.getName());
		assertEquals("description updated", filter.getDescription());
	}

	@Test
	public void delete_emailFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllEmailFilters().size());
		repository.saveEmailFilter("eric_thompson@facebook.com", "", getPlatformAccountGroupIds());
		final EmailFilter saved = repository.saveEmailFilter("ema@yahoo.com", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(2, repository.findAllFilters().size());
		assertEquals(2, repository.findAllEmailFilters().size());

		repository.deleteEmailFilter(saved.getId());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllEmailFilters().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoName_emailFilter() {

		final EmailFilter saved = repository.saveEmailFilter("fischer@gmail.com", "desc",
				getPlatformAccountGroupIds());
		repository.updateEmailFilter(saved.getId(), "", "description updated", getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNull_emailFilter() {

		final EmailFilter saved = repository.saveEmailFilter("fischer@gmail.com", "desc",
				getPlatformAccountGroupIds());
		repository.updateEmailFilter(saved.getId(), null, "description updated", getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoId_emailFilter() {
		repository.saveEmailFilter("tylor@gmail.com", "desc", getPlatformAccountGroupIds());
		repository.updateEmailFilter(null, "name_updated@gmail.com", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void saveWithTwoAtsEmailAddress_emailFilter() {
		repository.saveEmailFilter("tylor@gmail.com@yahoo.com", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveWithNoAtEmailAddress_emailFilter() {
		repository.saveEmailFilter("hello", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoAt_emailFilter() {
		repository.saveEmailFilter("tylor@gmail.com", "desc", getPlatformAccountGroupIds());
		repository.updateEmailFilter(null, "name_updated", "description updated", getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithTwoAts_emailFilter() {
		repository.saveEmailFilter("tylor@gmail.com", "desc", getPlatformAccountGroupIds());
		repository.updateEmailFilter(null, "email@wrong@gmail.com", "description updated",
				getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNonExistantId() {
		repository.updateEmailFilter(3L, "new@google.com", "desc", getPlatformAccountGroupIds());
	}

	// phone filter tests

	@Test
	public void saveAndRead_phoneFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPhoneFilters().size());
		final PhoneFilter saved = repository.savePhoneFilter("+43 660 904 9071", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllPhoneFilters().size());

		assertNotNull(saved.getId());
		assertEquals("00436609049071", saved.getName());
		assertEquals("desc", saved.getDescription());
		assertEquals(saved.getId(), repository.findAllFilters().get(0).getId());
		assertEquals(saved.getId(), repository.findAllPhoneFilters().get(0).getId());

		PhoneFilter filter = repository.findPhoneFilter(saved.getId());

		assertNotNull(filter);
		assertEquals(saved.getId(), filter.getId());
		assertEquals("00436609049071", filter.getName());
		assertEquals("desc", filter.getDescription());
	}

	@Test
	public void saveMinimumSizeWithPlus_phoneFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPhoneFilters().size());
		final PhoneFilter saved = repository.savePhoneFilter("+555", "desc", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllPhoneFilters().size());

		assertNotNull(saved.getId());
		assertEquals("00555", saved.getName());
		assertEquals("desc", saved.getDescription());
		assertEquals(saved.getId(), repository.findAllFilters().get(0).getId());
		assertEquals(saved.getId(), repository.findAllPhoneFilters().get(0).getId());
	}

	@Test
	public void saveMinimumSize_phoneFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPhoneFilters().size());
		final PhoneFilter saved = repository.savePhoneFilter("12345", "desc", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllPhoneFilters().size());

		assertNotNull(saved.getId());
		assertEquals("12345", saved.getName());
		assertEquals("desc", saved.getDescription());
		assertEquals(saved.getId(), repository.findAllFilters().get(0).getId());
		assertEquals(saved.getId(), repository.findAllPhoneFilters().get(0).getId());
	}

	@Test
	public void updateAndRead_phoneFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPhoneFilters().size());

		final PhoneFilter saved = repository.savePhoneFilter("+43 660 904 9071", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllPhoneFilters().size());

		final PhoneFilter updated = repository.updatePhoneFilter(saved.getId(), "+43 555 666 7777",
				"description updated", getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals("00435556667777", updated.getName());
		assertEquals("description updated", updated.getDescription());

		PhoneFilter filter = repository.findPhoneFilter(updated.getId());

		assertNotNull(filter);
		assertEquals(updated.getId(), filter.getId());
		assertEquals("00435556667777", filter.getName());
		assertEquals("description updated", filter.getDescription());
	}

	@Test
	public void delete_phoneFilter() {
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPhoneFilters().size());
		final PhoneFilter saved = repository.savePhoneFilter("+43 555 666 7777", "desc",
				getPlatformAccountGroupIds());
		refreshCaches();
		assertEquals(1, repository.findAllFilters().size());
		assertEquals(1, repository.findAllPhoneFilters().size());

		repository.deletePhoneFilter(saved.getId());
		refreshCaches();
		assertEquals(0, repository.findAllFilters().size());
		assertEquals(0, repository.findAllPhoneFilters().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoName_phoneFilter() {

		final PhoneFilter saved = repository.savePhoneFilter("+43 555 666 7777", "desc",
				getPlatformAccountGroupIds());
		repository.updateEmailFilter(saved.getId(), "", "description updated", getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void updateWithNoId_phoneFilter() {
		repository.savePhoneFilter("+43 555 666 7777", "desc", getPlatformAccountGroupIds());
		repository.updatePhoneFilter(null, "+43 555 666 8888", "description updated",
				getPlatformAccountGroupIds());

	}

	@Test(expected = IllegalArgumentException.class)
	public void saveWithMultiplePlusSigns_phoneFilter() {
		repository.savePhoneFilter("+43 777 8+8 9999", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveWithPlusSignInMiddle_phoneFilter() {
		repository.savePhoneFilter("777 8+8 9999", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveEmpty_phoneFilter() {
		repository.savePhoneFilter("           ", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveShortPhone_phoneFilter() {
		repository.savePhoneFilter("1234", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveShortPhoneWithSpaces_phoneFilter() {
		repository.savePhoneFilter(" 4444     ", "desc", getPlatformAccountGroupIds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void saveShortPhoneWithSpacesAndTab_phoneFilter() {
		repository.savePhoneFilter(" 4444  \t\t\t\t   ", "desc", getPlatformAccountGroupIds());
	}

	@Test
	public void retrieveFilterHistoryWithWrongFilterId() {
		List<FilterHistory> historyList = repository.retrieveHistory(1L);
		assertEquals(0, historyList.size());
	}

	@Test
	public void retrievePhoneFilterHistoryAfterAdd() {
		PhoneFilter filter = repository.savePhoneFilter("+43 555 666 7777", "desc", getPlatformAccountGroupIds());

		List<FilterHistory> historyList = repository.retrieveHistory(filter.getId());

		assertEquals(1, historyList.size());
		assertEquals("00435556667777", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("PHONE", historyList.get(0).getType());
		assertEquals(filter.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());
	}

	@Test
	public void retrievePhoneFilterHistoryAfterAddAndModify() {
		PhoneFilter filterAdded = repository.savePhoneFilter("+43 555 666 7777", "desc",
				getPlatformAccountGroupIds());
		PhoneFilter filterUpdated = repository.updatePhoneFilter(filterAdded.getId(), "+43 555 666 8888",
				"new desc", getPlatformAccountGroupIds());

		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(2, historyList.size());
		assertEquals("00435556667777", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("PHONE", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("00435556668888", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("PHONE", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());
	}

	@Test
	public void retrievePhoneFilterHistoryAfterAddModifyAndDelete() {
		PhoneFilter filterAdded = repository.savePhoneFilter("+43 555 666 7777", "desc",
				getPlatformAccountGroupIds());
		PhoneFilter filterUpdated = repository.updatePhoneFilter(filterAdded.getId(), "+43 555 666 8888",
				"new desc", getPlatformAccountGroupIds());
		repository.deletePhoneFilter(filterAdded.getId());

		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(3, historyList.size());
		assertEquals("00435556667777", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("PHONE", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("00435556668888", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("PHONE", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());

		assertEquals("00435556668888", historyList.get(2).getName());
		assertEquals("DELETE", historyList.get(2).getAction());
		assertEquals("PHONE", historyList.get(2).getType());
		assertEquals(filterUpdated.getId(), historyList.get(2).getFilterId());
		assertNotNull(historyList.get(2).getModified());
		assertEquals("user", historyList.get(2).getModifiedBy());

	}

	@Test
	public void retrieveStreetFilterHistoryAfterAdd() {
		StreetFilter filter = repository.saveStreetFilter("boulevard spain", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filter.getId());

		assertEquals(1, historyList.size());
		assertEquals("boulevard spain", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("STREET", historyList.get(0).getType());
		assertEquals(filter.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());
	}

	@Test
	public void retrieveStreetFilterHistoryAfterAddAndModify() {
		StreetFilter filterAdded = repository.saveStreetFilter("boulevard spain", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		StreetFilter filterUpdated = repository.updateStreetFilter(filterAdded.getId(), "boulevard texas", "de",
				"1020", "new desc", getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(2, historyList.size());
		assertEquals("boulevard spain", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("STREET", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("boulevard texas", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("STREET", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());
	}

	@Test
	public void retrieveStreetFilterHistoryAfterAddModifyAndDelete() {
		StreetFilter filterAdded = repository.saveStreetFilter("boulevard spain", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		StreetFilter filterUpdated = repository.updateStreetFilter(filterAdded.getId(), "boulevard texas", "de",
				"1020", "new desc", getPlatformAccountGroupIds());
		repository.deleteStreetFilter(filterAdded.getId());

		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(3, historyList.size());
		assertEquals("boulevard spain", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("STREET", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("boulevard texas", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("STREET", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());

		assertEquals("boulevard texas", historyList.get(2).getName());
		assertEquals("DELETE", historyList.get(2).getAction());
		assertEquals("STREET", historyList.get(2).getType());
		assertEquals(filterUpdated.getId(), historyList.get(2).getFilterId());
		assertNotNull(historyList.get(2).getModified());
		assertEquals("user", historyList.get(2).getModifiedBy());
	}

	@Test
	public void retrievePersonFilterHistoryAfterAdd() {
		PersonFilter filter = repository.savePersonFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filter.getId());

		assertEquals(1, historyList.size());
		assertEquals("fischer", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("PERSON", historyList.get(0).getType());
		assertEquals(filter.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());
	}

	@Test
	public void retrievePersonFilterHistoryAfterAddAndModify() {
		PersonFilter filterAdded = repository.savePersonFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		PersonFilter filterUpdated = repository.updatePersonFilter(filterAdded.getId(), "john", "de", "1020",
				"new desc", getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(2, historyList.size());
		assertEquals("fischer", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("PERSON", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("john", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("PERSON", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());
	}

	@Test
	public void retrievePersonFilterHistoryAfterAddModifyAndDelete() {
		PersonFilter filterAdded = repository.savePersonFilter("fischer", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		PersonFilter filterUpdated = repository.updatePersonFilter(filterAdded.getId(), "john", "de", "1020",
				"new desc", getPlatformAccountGroupIds());
		repository.deletePersonFilter(filterAdded.getId());

		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(3, historyList.size());
		assertEquals("fischer", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("PERSON", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("john", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("PERSON", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());

		assertEquals("john", historyList.get(2).getName());
		assertEquals("DELETE", historyList.get(2).getAction());
		assertEquals("PERSON", historyList.get(2).getType());
		assertEquals(filterUpdated.getId(), historyList.get(2).getFilterId());
		assertNotNull(historyList.get(2).getModified());
		assertEquals("user", historyList.get(2).getModifiedBy());
	}

	@Test
	public void retrieveEmailFilterHistoryAfterAdd() {
		EmailFilter filter = repository.saveEmailFilter("johnstockton@gmail.com", "desc",
				getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filter.getId());

		assertEquals(1, historyList.size());
		assertEquals("johnstockton@gmail.com", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("EMAIL", historyList.get(0).getType());
		assertEquals(filter.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());
	}

	@Test
	public void retrieveEmailFilterHistoryAfterAddAndModify() {
		EmailFilter filterAdded = repository.saveEmailFilter("johnstockton@gmail.com", "desc",
				getPlatformAccountGroupIds());
		EmailFilter filterUpdated = repository.updateEmailFilter(filterAdded.getId(), "johndoe@gmail.com",
				"new desc", getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(2, historyList.size());
		assertEquals("johnstockton@gmail.com", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("EMAIL", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("johndoe@gmail.com", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("EMAIL", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(0).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());
	}

	@Test
	public void retrieveEmailFilterHistoryAfterAddModifyAndDelete() {
		EmailFilter filterAdded = repository.saveEmailFilter("johnstockton@gmail.com", "desc",
				getPlatformAccountGroupIds());
		EmailFilter filterUpdated = repository.updateEmailFilter(filterAdded.getId(), "johndoe@gmail.com",
				"new desc", getPlatformAccountGroupIds());
		repository.deleteEmailFilter(filterAdded.getId());

		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(3, historyList.size());
		assertEquals("johnstockton@gmail.com", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("EMAIL", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());

		assertEquals("johndoe@gmail.com", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("EMAIL", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());

		assertEquals("johndoe@gmail.com", historyList.get(2).getName());
		assertEquals("DELETE", historyList.get(2).getAction());
		assertEquals("EMAIL", historyList.get(2).getType());
		assertEquals(filterUpdated.getId(), historyList.get(2).getFilterId());
		assertNotNull(historyList.get(2).getModified());
		assertEquals("user", historyList.get(2).getModifiedBy());
	}

	@Test
	public void retrieveCompanyFilterHistoryAfterAdd() {
		CompanyFilter filter = repository.saveCompanyFilter("google", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filter.getId());

		assertEquals(1, historyList.size());
		assertEquals("google", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("COMPANY", historyList.get(0).getType());
		assertEquals(filter.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertEquals("user", historyList.get(0).getModifiedBy());
	}

	@Test
	public void retrieveCompanyFilterHistoryAfterAddAndModify() {
		CompanyFilter filterAdded = repository.saveCompanyFilter("google", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		CompanyFilter filterUpdated = repository.updateCompanyFilter(filterAdded.getId(), "yahoo", "de", "1020",
				"desc", getPlatformAccountGroupIds());
		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(2, historyList.size());
		assertEquals("google", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("COMPANY", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertNotNull(historyList.get(0).getModifiedBy());

		assertEquals("yahoo", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("COMPANY", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());
	}

	@Test
	public void retrieveCompanyFilterHistoryAfterAddModifyAndDelete() {
		CompanyFilter filterAdded = repository.saveCompanyFilter("google", "de", "1010", "desc",
				getPlatformAccountGroupIds());
		CompanyFilter filterUpdated = repository.updateCompanyFilter(filterAdded.getId(), "yahoo", "de", "1020",
				"desc", getPlatformAccountGroupIds());
		repository.deleteCompanyFilter(filterAdded.getId());

		List<FilterHistory> historyList = repository.retrieveHistory(filterAdded.getId());

		assertEquals(3, historyList.size());
		assertEquals("google", historyList.get(0).getName());
		assertEquals("ADD", historyList.get(0).getAction());
		assertEquals("COMPANY", historyList.get(0).getType());
		assertEquals(filterAdded.getId(), historyList.get(0).getFilterId());
		assertNotNull(historyList.get(0).getModified());
		assertNotNull(historyList.get(0).getModifiedBy());

		assertEquals("yahoo", historyList.get(1).getName());
		assertEquals("MODIFY", historyList.get(1).getAction());
		assertEquals("COMPANY", historyList.get(1).getType());
		assertEquals(filterUpdated.getId(), historyList.get(1).getFilterId());
		assertEquals(historyList.get(1).getFilterId(), historyList.get(1).getFilterId());
		assertNotNull(historyList.get(1).getModified());
		assertEquals("user", historyList.get(1).getModifiedBy());

		assertEquals("yahoo", historyList.get(2).getName());
		assertEquals("DELETE", historyList.get(2).getAction());
		assertEquals("COMPANY", historyList.get(2).getType());
		assertEquals(filterUpdated.getId(), historyList.get(2).getFilterId());
		assertNotNull(historyList.get(2).getModified());
		assertEquals("user", historyList.get(2).getModifiedBy());
	}

	protected void createTestZipCodes() {
		TestZipCodeCreator.createZipCode("1010", "Wien", "DE", "48.2077", "16.3705");
		TestZipCodeCreator.createZipCode("1020", "Wien", "DE", "48.2167", "16.4000");
		TestZipCodeCreator.createZipCode("1030", "Wien", "DE", "48.1981", "16.3948");
		TestZipCodeCreator.createZipCode("1040", "Wien", "DE", "48.192", "16.3671");
		TestZipCodeCreator.createZipCode("1050", "Wien", "DE", "48.192", "16.3671");
		TestZipCodeCreator.createZipCode("9992", "Iselsberg-Stronach", "DE", "46.8357", "12.8497");
	}

	protected abstract Long[] getPlatformAccountGroupIds();

	protected void refreshCaches() {
	}
}
