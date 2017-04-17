package model.zipcode;

public interface ZipCodeRepository {

	ZipCode getZipCode(String countryCode, String zip);

	ZipCode saveZipCode(String zip, String placeName, String countryCode, String latitude, String longitude);

}
