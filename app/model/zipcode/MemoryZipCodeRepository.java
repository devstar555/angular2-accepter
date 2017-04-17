package model.zipcode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

class MemoryZipCodeRepository implements ZipCodeRepository {

	private List<ZipCode> values = new ArrayList<>();
	private final AtomicLong zipCodeCounter = new AtomicLong(1);

	@Override
	public ZipCode getZipCode(String countryCode, String zip) {
		return values.stream().filter(z -> z.getCountryCode().equals(countryCode) && z.getZip().equals(zip)).findFirst()
				.orElse(null);
	}

	@Override
	public ZipCode saveZipCode(String zip, String placeName, String countryCode,
			String latitude, String longitude) {

		String newCountryCode = Validator.validateAndFormatCountry(countryCode);
		String newZip = Validator.validateAndFormatZip(zip);
		String newLatitude = Validator.validateAndFormatLatitude(latitude);
		String newLongitude = Validator.validateAndFormatLongitude(longitude);

		ZipCode zipCode = new MemoryZipCode(zipCodeCounter.getAndIncrement(), newZip, placeName, newCountryCode,
				newLatitude, newLongitude);
		values.add(zipCode);
		return zipCode;
	}
}
