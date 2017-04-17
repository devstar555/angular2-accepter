package model.filter;

class MemoryCompanyFilter extends CompanyFilter {
	private Long id;

	MemoryCompanyFilter(Long id, String name, String country, String zipCode, String description,
			Long[] platformAccountGroupIds) {
		super(name, country, zipCode, description, platformAccountGroupIds);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
