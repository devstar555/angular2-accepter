package db;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class DbFilterHistory extends Model {
	public static final Finder<Long, DbFilterHistory> FINDER = new Finder<>(DbFilterHistory.class);
	public static final String COLUMN_ID = "filterId";
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private Long filterId;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private DbFilterType type;
	@Column(nullable = false)
	private DbFilterAction action;
	@Column(nullable = false)
	private LocalDateTime modified;
	private String modifiedBy;


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

	public Long getFilterId() {
		return filterId;
	}

	public void setFilterId(Long filterId) {
		this.filterId = filterId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DbFilterAction getAction() {
		return action;
	}

	public void setAction(DbFilterAction action) {
		this.action = action;
	}

	public LocalDateTime getModified() {
		return modified;
	}

	public void setModified(LocalDateTime modified) {
		this.modified = modified;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

}
