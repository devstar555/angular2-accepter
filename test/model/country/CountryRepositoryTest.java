package model.country;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class CountryRepositoryTest {

	protected abstract CountryRepository newRepository();

	private static final int NUMBER_OF_COUNTRIES = 248;

	private CountryRepository repository;

	@Before
	public void beforeRepositoryTest() {
		this.repository = newRepository();
	}

	@After
	public void afterRepositoryTest() {
		this.repository = null;
	}

	@Test
	public void testSizeOfList() {
		List<Country> countries = repository.getCountries();
		assertEquals(NUMBER_OF_COUNTRIES, countries.size());

	}

	@Test
	public void testElementOfList() {
		List<Country> countries = repository.getCountries();
		for (Country c : countries) {
			if (c.getCode().equals("DE")) {
				assertEquals("Germany", c.getName());
			}
		}

	}

	@Test
	public void testValidCountriesExistingValuesOfList() {
		assertTrue(repository.countryIsValid("BR")); // Brazil
		assertTrue(repository.countryIsValid("UY")); // Uruguay
		assertTrue(repository.countryIsValid("DE")); // Germany
		assertTrue(repository.countryIsValid("AF")); // Afghanistan
		assertTrue(repository.countryIsValid("ZW")); // Zimbabwe

	}

	@Test
	public void testNonValidCountries() {
		assertFalse(repository.countryIsValid("XX"));
		assertFalse(repository.countryIsValid("YY"));
		assertFalse(repository.countryIsValid("ZZ"));

	}

}
