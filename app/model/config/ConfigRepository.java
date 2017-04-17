package model.config;

import java.util.List;

public interface ConfigRepository {
	public String getValue(String propertyName);
	public void setValue(String propertyName, String value);
	public List<Property> findAllProperties();
	public boolean propertyExists(String propertyName);
}
