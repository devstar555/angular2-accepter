package model.country;

class TextFileCountry extends Country {

	private Long id;

	TextFileCountry(Long id, String code, String name) {
		this.id = id;
		this.setCode(code);
		this.setName(name);
	}

	@Override
	public Long getId() {
		return id;
	}

}
