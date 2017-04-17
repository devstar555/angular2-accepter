package db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.avaje.ebean.Model;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "zip", "country_code" }) })
public class DbZipCode extends Model {

	public static final Finder<Long, DbZipCode> FINDER = new Finder<>(DbZipCode.class);
	public static final String COLUMN_ZIP = "zip";
	public static final String COLUMN_COUNTRY_CODE = "countryCode";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String zip;
	@Column(nullable = false)
	private String countryCode;
	private String placeName;
	@Column(nullable = false)
	private String latitude;
	@Column(nullable = false)
	private String longitude;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
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
