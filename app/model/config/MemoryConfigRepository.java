package model.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

class MemoryConfigRepository implements ConfigRepository {

	private Map<String, String> values = new HashMap<>();

	@Override
	public boolean propertyExists(String propertyName) {
		return DefinedProperty.exists(propertyName);
	}

	@Override
	public String getValue(String propertyName) {
		if (!DefinedProperty.exists(propertyName))
			throw new IllegalArgumentException("Property " + propertyName + " is unknown");

		if (values.containsKey(propertyName))
			return values.get(propertyName);

		return DefinedProperty.forName(propertyName).getDefaultValue();
	}

	@Override
	public void setValue(String propertyName, String value) {
		if (!DefinedProperty.exists(propertyName))
			throw new IllegalArgumentException("Property " + propertyName + " is unknown");

		value = StringUtils.trimToNull(value);
		if (!DefinedProperty.forName(propertyName).isValueValid(value))
			throw new IllegalArgumentException("Property " + propertyName + " does not accept value " + value);

		values.put(propertyName, value);
	}

	@Override
	public List<Property> findAllProperties() {
		final Map<String, Property> result = new TreeMap<>();
		for (DefinedProperty cur : DefinedProperty.values())
			result.put(cur.getName(), new Property(cur));

		for (String curPropertyName : result.keySet())
			if (values.containsKey(curPropertyName))
				result.put(curPropertyName, result.get(curPropertyName).withValue(values.get(curPropertyName)));

		return new ArrayList<>(result.values());
	}

}
