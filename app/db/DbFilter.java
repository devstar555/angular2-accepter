package db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.avaje.ebean.Model;

@Entity
public class DbFilter extends Model {

	public static final Finder<Long, DbFilter> FINDER = new Finder<>(DbFilter.class);
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_PLATFORM_ACCOUNT_GROUPS = "platformAccountGroups";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private DbFilterType type;

	private String value;
	private String country;
	private String zip;
	private String description;
	@ManyToMany(cascade = CascadeType.ALL)
	private List<DbPlatformAccountGroup> platformAccountGroups;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public DbFilterType getType() {
		return type;
	}

	public void setType(DbFilterType type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<DbPlatformAccountGroup> getPlatformAccountGroups() {
		return platformAccountGroups;
	}

	public void setPlatformAccountGroups(List<DbPlatformAccountGroup> platformAccountGroups) {
		this.platformAccountGroups = platformAccountGroups;
	}

	public Long[] getPlatformAccountGroupIds() {
		return platformAccountGroups.stream().map(DbPlatformAccountGroup::getId).toArray(Long[]::new);
	}

}
