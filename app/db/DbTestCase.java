package db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.UniqueConstraint;

import com.avaje.ebean.Model;

@Entity
@UniqueConstraint(columnNames = { DbTestCase.COLUMN_TEST_RUN_ID, DbTestCase.COLUMN_NAME })
public class DbTestCase extends Model {

	public static final Finder<Long, DbTestCase> FINDER = new Finder<>(DbTestCase.class);
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_EXPECTED = "expected";
	public static final String COLUMN_ACTUAL = "actual";
	public static final String COLUMN_TEST_RUN_ID = "db_test_run_id";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String expected;

	@Column(nullable = false)
	private String actual;

	public DbTestCase(String name, String expected, String actual) {
		this.name = name;
		this.expected = expected;
		this.actual = actual;
	}
	public DbTestCase() {
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getExpected() {
		return expected;
	}
	public void setExpected(String expected) {
		this.expected = expected;
	}
	public String getActual() {
		return actual;
	}
	public void setActual(String actual) {
		this.actual = actual;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

}
