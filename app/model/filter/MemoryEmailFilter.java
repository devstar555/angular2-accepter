package model.filter;

class MemoryEmailFilter extends EmailFilter {

	private Long id;

	MemoryEmailFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {
		super(name, description, platformAccountGroupIds);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
