package util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void testRemoveDuplicates() {
		assertEquals(0, Utils.removeDuplicates(Arrays.asList(), Integer::compare).size());
		assertEquals(1, Utils.removeDuplicates(Arrays.asList(55), Integer::compare).size());
		assertEquals(2, Utils.removeDuplicates(Arrays.asList(55, 99), Integer::compare).size());
		assertEquals(3, Utils.removeDuplicates(Arrays.asList(55, 99, 77), Integer::compare).size());
		assertEquals(2, Utils.removeDuplicates(Arrays.asList(55, 99, 55), Integer::compare).size());
		assertEquals(1, Utils.removeDuplicates(Arrays.asList(55, 55, 55), Integer::compare).size());
	}

	@Test
	public void testMapList() {
		assertEquals(0, Utils.mapList(new ArrayList<Long>(), x -> x + 1).size());
		assertEquals(1, Utils.mapList(Arrays.asList(1L), x -> x + 1).size());
		assertEquals((Long) 2L, Utils.mapList(Arrays.asList(1L), x -> x + 1).get(0));
	}

	@Test
	public void testGetDateBefore() {
		Date future = Utils.getDateBefore(-3600L);
		Date present = new Date();
		Date past = Utils.getDateBefore(3600L);
		assertTrue(past.before(present));
		assertTrue(present.before(future));
		// tolerance/variance of +/-100 ms to cover slow machines
		assertEquals(3600L * 1000, future.getTime() - present.getTime(), 100);
	}

	@Test
	public void testGetDistance() {
		assertEquals(2093.39d, Utils.getDistance("48.2077", "16.3705", "48.1981", "16.3948"), 0.01d);
	}

	@Test
	public void testLongArrayToString() {
		assertEquals(null, Utils.longArrayToString(null));
		assertEquals("", Utils.longArrayToString(new long[] {}));
		assertEquals("18818184", Utils.longArrayToString(new long[] { 18818184 }));
		assertEquals("18818184,17025228", Utils.longArrayToString(new long[] { 18818184, 17025228 }));
	}

	@Test
	public void testStringToLongArray() {
		assertEquals(null, Utils.stringToLongArray(null));
		assertArrayEquals(new long[] {}, Utils.stringToLongArray(""));
		assertArrayEquals(new long[] { 18818184 }, Utils.stringToLongArray("18818184"));
		assertArrayEquals(new long[] { 18818184, 17025228 }, Utils.stringToLongArray("18818184,17025228"));
	}
}
