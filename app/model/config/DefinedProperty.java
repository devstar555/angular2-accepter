package model.config;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

enum DefinedProperty {

	CLEAN_DATA("CLEAN_DATA", PropertyType.LONG, "" + (30l * 24 * 3600),
			"Specifies the number of seconds that data is ensured to stay in the system at least. "
					+ "API functions that delete data check if the user requests to remove entries, "
					+ "that are younger than the number of seconds specified in this property. "
					+ "If this is the case, an error will be returned."), ZIP_MAX_DISTANCE("ZIP_MAX_DISTANCE",
							PropertyType.INTEGER, String.valueOf(50000),
							"Specifies the largest allowed distance between order address zip code and filter zip code to match in meters. If the distance between between zip codes exceeds this value, then address will not be considered for match");

	private static final Map<String, DefinedProperty> BY_NAME;

	static {
		final Map<String, DefinedProperty> byName = new TreeMap<>();
		for (DefinedProperty cur : values())
			byName.put(cur.getName(), cur);

		BY_NAME = Collections.unmodifiableMap(byName);
	}

	static final boolean exists(String name) {
		return BY_NAME.containsKey(name);
	}

	static final DefinedProperty forName(String name) {
		return BY_NAME.get(name);
	}

	private final String name;
	private final String defaultValue;
	private final String description;
	private final PropertyType type;

	private DefinedProperty(String name, PropertyType type, String defaultValue, String description) {
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.description = description;
	}

	public boolean isValueValid(String value) {
		return type.isValueValid(value);
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public PropertyType getType() {
		return type;
	}

}
