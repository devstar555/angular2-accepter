package db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import com.avaje.ebean.Model;

@Entity
@UniqueConstraint(columnNames = { DbReviewReason.COLUMN_TYPE, DbReviewReason.COLUMN_VALUE })
public class DbReviewReason extends Model {

	public static final Finder<Long, DbReviewReason> FINDER = new Finder<>(DbReviewReason.class);
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_TYPE = "type";
	public static final String COLUMN_VALUE = "value";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String type;

	private String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
