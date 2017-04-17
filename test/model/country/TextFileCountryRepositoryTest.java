package model.country;

public class TextFileCountryRepositoryTest extends CountryRepositoryTest {

	@Override
	protected CountryRepository newRepository() {
		return new TextFileCountryRepository();
	}

}
