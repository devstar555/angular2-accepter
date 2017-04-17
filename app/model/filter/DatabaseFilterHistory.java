package model.filter;

import db.DbFilterHistory;

class DatabaseFilterHistory extends FilterHistory {

	private Long id;

	DatabaseFilterHistory(DbFilterHistory filterHistory) {

		this.id = filterHistory.getId();
		this.setName(filterHistory.getName());
		this.setAction(filterHistory.getAction().toString());
		this.setFilterId(filterHistory.getFilterId());
		this.setType(filterHistory.getType().toString());
		this.setModified(filterHistory.getModified().toString());
		this.setModifiedBy(filterHistory.getModifiedBy());
	}

	@Override
	public Long getId() {
		return id;
	}

}
