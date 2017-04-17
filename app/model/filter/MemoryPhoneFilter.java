package model.filter;

class MemoryPhoneFilter extends PhoneFilter {

	private Long id;

	MemoryPhoneFilter(Long id, String name, String description, Long[] platformAccountGroupIds) {
		super(name, description, platformAccountGroupIds);
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

}
