package model.zipcode;

import java.util.ArrayList;
import java.util.List;

import db.DbZipCode;

class DatabaseZipCodeRepository implements ZipCodeRepository {

	@Override
	public ZipCode getZipCode(final String countryCode, final String zip) {
		return DatabaseZipCodeCache.getZipCode(countryCode, zip);
	}

	@Override
	public ZipCode saveZipCode(final String zip, final String placeName, final String countryCode,
			final String latitude, final String longitude) {

		String newCountryCode = Validator.validateAndFormatCountry(countryCode);
		String newZip = Validator.validateAndFormatZip(zip);
		String newLatitude = Validator.validateAndFormatLatitude(latitude);
		String newLongitude = Validator.validateAndFormatLongitude(longitude);

		DbZipCode dbZipCode = new DbZipCode();
		dbZipCode.setCountryCode(newCountryCode);
		dbZipCode.setLatitude(newLatitude);
		dbZipCode.setLongitude(newLongitude);
		dbZipCode.setPlaceName(placeName);
		dbZipCode.setZip(newZip);

		dbZipCode.save();

		return new DatabaseZipCode(dbZipCode);
	}

	public List<ZipCode> findAll() {
		List<ZipCode> zipCodes = new ArrayList<>();
		for (DbZipCode dbZipCode : DbZipCode.FINDER.all()) {
			zipCodes.add(new DatabaseZipCode(dbZipCode));
		}

		return zipCodes;
	}
}
