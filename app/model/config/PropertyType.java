package model.config;

enum PropertyType {
	LONG, INTEGER;

	public boolean isValueValid(String value) {
		if (this == LONG) {
			try {
				Long.valueOf(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else if (this == INTEGER) {
			try {
				Integer.valueOf(value);
				return true;
			} catch (NumberFormatException e) {
				return false;
			}
		} else {
			throw new RuntimeException("Unknown type " + this);
		}
	}
}
