package model.filter;

class MemoryStreetFilter extends StreetFilter {
	private Long id;

	MemoryStreetFilter(Long id, String name, String country, String zip, String description,
			Long[] platformAccountGroupIds) {
		super(name, country, zip, description, platformAccountGroupIds);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
