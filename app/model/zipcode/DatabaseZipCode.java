package model.zipcode;

import db.DbZipCode;

class DatabaseZipCode extends ZipCode {

	private Long id;

	DatabaseZipCode(DbZipCode zipCode) {
		super(zipCode.getZip(), zipCode.getPlaceName(), zipCode.getCountryCode(),
				zipCode.getLatitude(), zipCode.getLongitude());
		this.id = zipCode.getId();
	}

	@Override
	public Long getId() {
		return id;
	}

}
