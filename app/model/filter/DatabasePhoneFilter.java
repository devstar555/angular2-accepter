package model.filter;

import db.DbFilter;
import db.DbFilterType;

class DatabasePhoneFilter extends PhoneFilter {

	private Long id;

	DatabasePhoneFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {
		super(name, description, platformAccountGroupIds);
		this.id = id;
	}

	DatabasePhoneFilter(DbFilter filter) {
		if (filter.getType() == null)
			throw new IllegalArgumentException("filter is required");
		if (!filter.getType().equals(DbFilterType.PHONE))
			throw new IllegalArgumentException("filter has type " + filter.getType());

		this.id = filter.getId();
		this.setName(filter.getValue());
		this.setDescription(filter.getDescription());
		this.setPlatformAccountGroupIds(filter.getPlatformAccountGroupIds());
	}

	@Override
	public Long getId() {
		return id;
	}
}
