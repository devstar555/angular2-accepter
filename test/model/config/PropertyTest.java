package model.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.UUID;

import org.junit.Test;

public class PropertyTest {

	@Test
	public void fromDefinedProperty() {
		for (DefinedProperty base : DefinedProperty.values()) {
			Property property = new Property(base);
			assertEquals(base.getName(), property.getName());
			assertEquals(base.getDefaultValue(), property.getValue());
			assertEquals(base.getDescription(), property.getDescription());
		}
	}

	@Test
	public void simple() {
		final Property property = new Property("n", "v", "d");
		assertEquals("n", property.getName());
		assertEquals("v", property.getValue());
		assertEquals("d", property.getDescription());
	}

	@Test
	public void withValue() {
		final Property base = new Property("n", "v", "d");
		final Property child = base.withValue("v2");
		assertEquals("v", base.getValue());
		assertEquals("v2", child.getValue());
	}

	@Test
	public void equalsHashCompare() {
		final Property foo1 = new Property("foo", UUID.randomUUID().toString(), UUID.randomUUID().toString());
		final Property foo2 = new Property("foo", UUID.randomUUID().toString(), UUID.randomUUID().toString());
		final Property bar = new Property("bar", UUID.randomUUID().toString(), UUID.randomUUID().toString());

		assertEquals(0, foo1.compareTo(foo1));
		assertTrue(foo1.equals(foo1));
		assertEquals(foo1.hashCode(), foo1.hashCode());

		assertEquals(0, foo1.compareTo(foo2));
		assertTrue(foo1.equals(foo2));
		assertEquals(foo1.hashCode(), foo2.hashCode());

		assertNotEquals(0, foo1.compareTo(bar));
		assertFalse(bar.equals(foo2));
		assertNotEquals(bar.hashCode(), foo2.hashCode());
	}
}
