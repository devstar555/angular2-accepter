package model.filter;

import db.DbFilter;
import db.DbFilterType;

class DatabaseCompanyFilter extends CompanyFilter {

	private Long id;

	DatabaseCompanyFilter(DbFilter filter) {
		if (filter.getType() == null)
			throw new IllegalArgumentException("filter is required");
		if (!filter.getType().equals(DbFilterType.COMPANY))
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
