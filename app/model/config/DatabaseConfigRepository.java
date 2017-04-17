package model.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import db.DbConfigProperty;

class DatabaseConfigRepository implements ConfigRepository {

	@Override
	public String getValue(String propertyName) {
		if (!DefinedProperty.exists(propertyName))
			throw new IllegalArgumentException("Property " + propertyName + " is unknown");

		String value = DatabaseConfigCache.getValue(propertyName);

		return value != null ? value : DefinedProperty.forName(propertyName).getDefaultValue();
	}

	@Override
	public void setValue(String propertyName, String value) {
		if (!DefinedProperty.exists(propertyName))
			throw new IllegalArgumentException("Property " + propertyName + " is unknown");

		value = StringUtils.trimToNull(value);
		if (!DefinedProperty.forName(propertyName).isValueValid(value))
			throw new IllegalArgumentException("Property " + propertyName + " does not accept value " + value);

		final DbConfigProperty db = DbConfigProperty.FINDER.where().eq(DbConfigProperty.COLUMN_NAME, propertyName)
				.findUnique();

		if (db != null) {

			// update existing db entry
			db.setValue(value);
			db.update();

		} else {

			// create new db entry
			final DbConfigProperty newDb = new DbConfigProperty();
			newDb.setName(propertyName);
			newDb.setValue(value);
			newDb.save();

		}
	}

	@Override
	public List<Property> findAllProperties() {
		final Map<String, Property> result = new TreeMap<>();
		for (DefinedProperty cur : DefinedProperty.values())
			result.put(cur.getName(), new Property(cur));

		for (DbConfigProperty cur : DbConfigProperty.FINDER.all())
			if (result.containsKey(cur.getName()))
				result.put(cur.getName(), result.get(cur.getName()).withValue(cur.getValue()));

		return new ArrayList<>(result.values());
	}

	@Override
	public boolean propertyExists(String propertyName) {
		return DefinedProperty.exists(propertyName);
	}

}
