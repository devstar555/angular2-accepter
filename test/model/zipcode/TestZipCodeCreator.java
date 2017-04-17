package model.zipcode;

public class TestZipCodeCreator {

	public static ZipCode createZipCode(String zip, String placeName, String countryCode,
			String latitude, String longitude) {

		return ZipCodeRepositoryFactory.get().saveZipCode(zip, placeName, countryCode, latitude,
				longitude);
	}
}
