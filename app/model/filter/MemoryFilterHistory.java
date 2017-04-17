package model.filter;

import java.time.LocalDateTime;

class MemoryFilterHistory extends FilterHistory {

	private Long id;

	MemoryFilterHistory(Long id, String name, String action, Long filterId, LocalDateTime modified, String type,
			String modifiedBy) {

		this.id = id;
		this.setName(name);
		this.setAction(action);
		this.setFilterId(filterId);
		this.setModified(modified.toString());
		this.setType(type);
		this.setModifiedBy(modifiedBy);
	}

	@Override
	public Long getId() {
		return id;
	}

}
