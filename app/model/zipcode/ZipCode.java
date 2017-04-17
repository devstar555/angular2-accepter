package model.zipcode;

public abstract class ZipCode {

	private String zip;
	private String countryCode;
	private String placeName;
	private String latitude;
	private String longitude;

	ZipCode(String zip, String placeName, String countryCode, String latitude,
			String longitude) {
		this.zip = zip;
		this.placeName = placeName;
		this.countryCode = countryCode;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public abstract Long getId();

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

}
