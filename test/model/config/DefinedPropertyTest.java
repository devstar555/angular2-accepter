package model.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

public class DefinedPropertyTest {

	@Test
	public void valuesValid() {
		assertTrue(DefinedProperty.CLEAN_DATA.isValueValid("123456"));
		assertFalse(DefinedProperty.CLEAN_DATA.isValueValid("123456asdf"));
	}

	@Test
	public void defaultValuesAreValid() {
		for (DefinedProperty cur : DefinedProperty.values())
			assertTrue(cur.isValueValid(cur.getDefaultValue()));
	}

	@Test
	public void exists() {
		assertTrue(DefinedProperty.exists(DefinedProperty.CLEAN_DATA.getName()));
		assertFalse(DefinedProperty.exists(UUID.randomUUID().toString()));
	}

	@Test
	public void forName() {
		assertSame(DefinedProperty.CLEAN_DATA, DefinedProperty.forName(DefinedProperty.CLEAN_DATA.getName()));
	}

}
