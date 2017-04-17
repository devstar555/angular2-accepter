package db;

import java.util.Date;
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
public class DbReviewRequest extends Model {

	public static final Finder<Long, DbReviewRequest> FINDER = new Finder<>(DbReviewRequest.class);
	public static final String COLUMN_ID = "id";
	public static final String COLUMN_ORDERID = "orderId";
	public static final String COLUMN_REQUEST_DATE = "requestDate";
	public static final String MANY_TO_MANY_REASONS = "reasons";
	public static final String REQUEST_DATE = "requestDate";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private Long orderId;

	@Column(nullable = false)
	private Date requestDate;

	@ManyToMany(cascade = CascadeType.ALL)
	private List<DbReviewReason> reasons;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public List<DbReviewReason> getReasons() {
		return reasons;
	}

	public void setReasons(List<DbReviewReason> reasons) {
		this.reasons = reasons;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

}
