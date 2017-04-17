package model.filter;

import db.DbFilter;
import db.DbFilterType;

class DatabaseStreetFilter extends StreetFilter {

	private Long id;

	DatabaseStreetFilter(DbFilter filter) {
		if (filter.getType() == null)
			throw new IllegalArgumentException("filter is required");
		if (!filter.getType().equals(DbFilterType.STREET))
			throw new IllegalArgumentException("filter has type " + filter.getType());

		this.id = filter.getId();
		this.setName(filter.getValue());
		this.setCountry(filter.getCountry());
		this.setZip(filter.getZip());
		this.setDescription(filter.getDescription());
		this.platformAccountGroupIds = filter.getPlatformAccountGroupIds();
	}

	@Override
	public Long getId() {
		return id;
	}

}
