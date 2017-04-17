package model.filter;

import db.DbFilter;
import db.DbFilterType;

class DatabaseEmailFilter extends EmailFilter {

	private Long id;

	DatabaseEmailFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {
		super(name, description, platformAccountGroupIds);
		this.id = id;
	}

	DatabaseEmailFilter(DbFilter filter) {
		if (filter.getType() == null)
			throw new IllegalArgumentException("filter is required");
		if (!filter.getType().equals(DbFilterType.EMAIL))
			throw new IllegalArgumentException("filter has type " + filter.getType());

		this.id = filter.getId();
		this.setName(filter.getValue());
		this.setDescription(filter.getDescription());
		this.platformAccountGroupIds = filter.getPlatformAccountGroupIds();
	}

	@Override
	public Long getId() {
		return id;
	}
}
