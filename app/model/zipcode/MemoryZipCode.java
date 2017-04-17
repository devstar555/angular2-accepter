package model.zipcode;

class MemoryZipCode extends ZipCode {

	private Long id;

	MemoryZipCode(Long id, String zip, String placeName, String countryCode, String latitude,
			String longitude) {
		super(zip, placeName, countryCode, latitude, longitude);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
