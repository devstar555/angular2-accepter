package db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class DbConfigProperty extends Model {

	public static final Finder<Long, DbConfigProperty> FINDER = new Finder<>(DbConfigProperty.class);
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_VALUE = "value";

	@Id
	private String name;

	@Column(nullable = false)
	private String value;

	public DbConfigProperty() {
	}

	public DbConfigProperty(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
