package model.config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PropertyTypeTest {

	@Test
	public void valueValidLong() {
		assertTrue(PropertyType.LONG.isValueValid("123456"));
		assertFalse(PropertyType.LONG.isValueValid("123456asdf"));
	}

}
