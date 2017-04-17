package model.zipcode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class ZipCodeRepositoryTest {

	protected abstract ZipCodeRepository newRepository();

	protected void refreshCaches() {

	}

	protected ZipCodeRepository repository;

	private static void resetModelFactories() {
		ZipCodeRepositoryFactory.FACTORY = ZipCodeRepositoryFactory
				.factoryCaching(ZipCodeRepositoryFactory.factoryMemory());
	}

	@Before
	public void beforeRepositoryTest() {
		resetModelFactories();
		this.repository = newRepository();
	}

	@After
	public void afterRepositoryTest() {
		this.repository = null;
		resetModelFactories();
	}

	@Test
	public void simple_saveZipCode() {
		ZipCode zipCode = repository.saveZipCode("1010", "Wien", "AT", "48.2077", "16.3705");
		assertNotNull(zipCode.getId());
		assertEquals("1010", zipCode.getZip());
		assertEquals("Wien", zipCode.getPlaceName());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("48.2077", zipCode.getLatitude());
		assertEquals("16.3705", zipCode.getLongitude());
	}

	@Test
	public void saveZipCodeWithZipHavingSpaces() {
		ZipCode zipCode = repository.saveZipCode("  1010  ", "Wien", "AT", "48.2077", "16.3705");

		assertNotNull(zipCode.getId());
		assertEquals("1010", zipCode.getZip());
		assertEquals("Wien", zipCode.getPlaceName());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("48.2077", zipCode.getLatitude());
		assertEquals("16.3705", zipCode.getLongitude());
	}

	@Test
	public void saveZipCodeWithCountryCodeHavingSpaces() {
		ZipCode zipCode = repository.saveZipCode("1010", "Wien", "  AT   ", "48.2077", "16.3705");

		assertNotNull(zipCode.getId());
		assertEquals("1010", zipCode.getZip());
		assertEquals("Wien", zipCode.getPlaceName());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("48.2077", zipCode.getLatitude());
		assertEquals("16.3705", zipCode.getLongitude());
	}

	@Test
	public void saveZipCodeWithLatitudeHavingSpaces() {
		ZipCode zipCode = repository.saveZipCode("1010", "Wien", "AT", "   48.2077    ", "16.3705");

		assertNotNull(zipCode.getId());
		assertEquals("1010", zipCode.getZip());
		assertEquals("Wien", zipCode.getPlaceName());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("48.2077", zipCode.getLatitude());
		assertEquals("16.3705", zipCode.getLongitude());
	}

	@Test
	public void saveZipCodeWithLongitudeHavingSpaces() {
		ZipCode zipCode = repository.saveZipCode("1010", "Wien", "AT", "48.2077", "    16.3705    ");

		assertNotNull(zipCode.getId());
		assertEquals("1010", zipCode.getZip());
		assertEquals("Wien", zipCode.getPlaceName());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("48.2077", zipCode.getLatitude());
		assertEquals("16.3705", zipCode.getLongitude());
	}

	@Test
	public void saveZipCodeWithHighestLatitudeAndLongitude() {
		ZipCode zipCode = repository.saveZipCode("1010", "Wien", "AT", "90.0000", "180.0000");

		assertNotNull(zipCode.getId());
		assertEquals("1010", zipCode.getZip());
		assertEquals("Wien", zipCode.getPlaceName());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("90.0000", zipCode.getLatitude());
		assertEquals("180.0000", zipCode.getLongitude());

	}

	@Test
	public void saveZipCodeWithLowestLatitudeAndLongitude() {
		ZipCode zipCode = repository.saveZipCode("1010", "Wien", "AT", "-90.0000", "-180.0000");

		assertNotNull(zipCode.getId());
		assertEquals("1010", zipCode.getZip());
		assertEquals("Wien", zipCode.getPlaceName());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("-90.0000", zipCode.getLatitude());
		assertEquals("-180.0000", zipCode.getLongitude());
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidZip1() {
		repository.saveZipCode("", "Wien", "AT", "48.2077", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidZip2() {
		repository.saveZipCode("123456", "Wien", "AT", "48.2077", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidZip3() {
		repository.saveZipCode("a1b1c", "Wien", "AT", "48.2077", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidCountryCode1() {
		repository.saveZipCode("1010", "Wien", "zz", "48.2077", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidCountryCode2() {
		repository.saveZipCode("1010", "Wien", "", "48.2077", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidCountryCode3() {
		repository.saveZipCode("1010", "Wien", "Austria", "48.2077", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLatitude1() {
		repository.saveZipCode("1010", "Wien", "AT", "", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLatitude2() {
		repository.saveZipCode("1010", "Wien", "AT", "abcd", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLatitude3() {
		repository.saveZipCode("1010", "Wien", "AT", "-90.0001", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLatitude4() {
		repository.saveZipCode("1010", "Wien", "AT", "90.0001", "16.3705");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLongitude1() {
		repository.saveZipCode("1010", "Wien", "AT", "48.2077", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLongitude2() {
		repository.saveZipCode("1010", "Wien", "AT", "48.2077", "abcd");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLongitude3() {
		repository.saveZipCode("1010", "Wien", "AT", "48.2077", "-180.0001");
	}

	@Test(expected = IllegalArgumentException.class)
	public void simple_saveZipCodeWithInvalidLongitude4() {
		repository.saveZipCode("1010", "Wien", "AT", "48.2077", "180.0001");
	}

	@Test
	public void getZipCodeWithNoData() {
		ZipCode zipCode = repository.getZipCode("CH", "1212");
		assertNull(zipCode);
	}

	@Test
	public void getZipCodeWithData() {
		repository.saveZipCode("1010", "Wien", "AT", "48.2077", "16.3705");
		refreshCaches();
		ZipCode actual = repository.getZipCode("AT", "1010");

		assertNotNull(actual.getId());
		assertEquals("1010", actual.getZip());
		assertEquals("Wien", actual.getPlaceName());
		assertEquals("AT", actual.getCountryCode());
		assertEquals("48.2077", actual.getLatitude());
		assertEquals("16.3705", actual.getLongitude());
	}
}
