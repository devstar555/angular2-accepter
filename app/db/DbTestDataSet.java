package db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class DbTestDataSet extends Model {

	public static final Finder<Long, DbTestDataSet> FINDER = new Finder<>(DbTestDataSet.class);
	public static final String COLUMN_ID = "id";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long timeoutMilliseconds;

	@Column(nullable = false, length = 1000)
	private String expectAccept;

	@Column(nullable = false, length = 1000)
	private String expectReview;

	@Column(nullable = false, length = 1000)
	private String performance;

	public DbTestDataSet(Long timeoutMilliseconds, String expectAccept, String expectReview, String performance) {
		this.timeoutMilliseconds = timeoutMilliseconds;
		this.expectAccept = expectAccept;
		this.expectReview = expectReview;
		this.performance = performance;
	}
	public DbTestDataSet() {
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getTimeoutMilliseconds() {
		return timeoutMilliseconds;
	}
	public void setTimeoutMilliseconds(Long timeoutMilliseconds) {
		this.timeoutMilliseconds = timeoutMilliseconds;
	}
	public String getExpectAccept() {
		return expectAccept;
	}
	public void setExpectAccept(String expectAccept) {
		this.expectAccept = expectAccept;
	}
	public String getExpectReview() {
		return expectReview;
	}
	public void setExpectReview(String expectReview) {
		this.expectReview = expectReview;
	}
	public String getPerformance() {
		return performance;
	}
	public void setPerformance(String performance) {
		this.performance = performance;
	}
}
