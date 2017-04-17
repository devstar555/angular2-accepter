package model.filter;

import db.DbFilter;
import db.DbFilterType;

class DatabasePersonFilter extends PersonFilter {

	private Long id;

	DatabasePersonFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		super(name, country, zip, description, platformAccountGroupIds);
		this.id = id;
	}

	DatabasePersonFilter(DbFilter filter) {
		if (filter.getType() == null)
			throw new IllegalArgumentException("filter is required");
		if (!filter.getType().equals(DbFilterType.PERSON))
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
