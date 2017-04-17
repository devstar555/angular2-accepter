package model.filter;

class MemoryPersonFilter extends PersonFilter {
	private Long id;

	MemoryPersonFilter(Long id, String name, String country, String zipCode, String description,
			Long[] platformAccountGroupIds) {
		super(name, country, zipCode, description, platformAccountGroupIds);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
