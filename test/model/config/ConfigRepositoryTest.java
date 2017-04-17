package model.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class ConfigRepositoryTest {

	protected abstract ConfigRepository newRepository();

	private ConfigRepository repository;

	@Before
	public void beforeRepositoryTest() {
		this.repository = newRepository();
	}

	@After
	public void afterRepositoryTest() {
		this.repository = null;
	}

	@Test
	public void findAllProperties() {
		final List<Property> result = repository.findAllProperties();
		assertEquals(DefinedProperty.values().length, result.size());
		for (Property cur : result) {
			final DefinedProperty base = DefinedProperty.forName(cur.getName());
			assertEquals(cur.getName(), base.getName());
			assertEquals(cur.getDescription(), base.getDescription());
			assertEquals(cur.getValue(), base.getDefaultValue());
		}
	}

	@Test
	public void propertyExists() {
		assertTrue(repository.propertyExists(DefinedProperty.values()[0].getName()));
		assertFalse(repository.propertyExists(UUID.randomUUID().toString()));
	}

	@Test
	public void getAndSetValue() {
		final DefinedProperty base = DefinedProperty.CLEAN_DATA;
		refreshCaches();
		assertEquals(base.getDefaultValue(), repository.getValue(base.getName()));
		repository.setValue(base.getName(), "500");
		refreshCaches();
		assertEquals("500", repository.getValue(base.getName()));
		repository.setValue(base.getName(), "22");
		refreshCaches();
		assertEquals("22", repository.getValue(base.getName()));
	}


	@Test(expected = IllegalArgumentException.class)
	public void getValueDoesNotExist() {
		repository.getValue(UUID.randomUUID().toString());
	}

	@Test(expected = IllegalArgumentException.class)
	public void setValueDoesNotExist() {
		repository.setValue(UUID.randomUUID().toString(), "123");
	}

	@Test(expected = IllegalArgumentException.class)
	public void setValueInvalidDataType() {
		repository.setValue(DefinedProperty.CLEAN_DATA.getName(), "asdf");
	}

	protected void refreshCaches() {
	}
}
