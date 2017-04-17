package db;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

@Entity
public class DbTestRun extends Model {

	public static final Finder<Long, DbTestRun> FINDER = new Finder<>(DbTestRun.class);
	public static final String ONE_TO_MANY_TESTCASES = "testCases";
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_START = "start";
	public static final String COLUMN_END = "end";
	public static final String ORDER_BY_START_DATE_DESC = COLUMN_START + " desc";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Date start;

	@Column(nullable = false)
	private Date end;

	@OneToMany(cascade = CascadeType.ALL)
	private List<DbTestCase> testCases;

	public DbTestRun() {
	}

	public DbTestRun(Date start, Date end, List<DbTestCase> cases) {
		this.start = start;
		this.end = end;
		this.testCases = cases;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public List<DbTestCase> getTestCases() {
		return testCases;
	}

	public void setTestCases(List<DbTestCase> testCases) {
		this.testCases = testCases;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
