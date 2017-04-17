package model.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class BundleTest {

	@Test
	public void isolateAddresses_noAddress() {
		final User user = user();
		final Bundle bundle = new Bundle(user, Collections.emptyList(), null);
		final List<Bundle> result = bundle.isolateAddresses();
		assertEquals(1, result.size());
		assertSame(user, result.get(0).getUser());
		assertEquals(0, result.get(0).getAddresses().size());
	}

	@Test
	public void isolateAddresses_singleAddress() {
		final User user = user();
		final Address address1 = address();
		final Bundle bundle = new Bundle(user, Arrays.asList(address1), null);
		final List<Bundle> result = bundle.isolateAddresses();
		assertEquals(2, result.size());

		assertSame(user, result.get(0).getUser());
		assertEquals(0, result.get(0).getAddresses().size());

		assertSame(user, result.get(1).getUser());
		assertEquals(1, result.get(1).getAddresses().size());
		assertSame(address1, result.get(1).getAddresses().get(0));
	}

	@Test
	public void isolateAddresses_multipleAddresses() {
		final User user = user();
		final Address address1 = address();
		final Address address2 = address();
		final Bundle bundle = new Bundle(user, Arrays.asList(address1, address2), null);
		final List<Bundle> result = bundle.isolateAddresses();
		assertEquals(3, result.size());

		assertSame(user, result.get(0).getUser());
		assertEquals(0, result.get(0).getAddresses().size());

		assertSame(user, result.get(1).getUser());
		assertEquals(1, result.get(1).getAddresses().size());
		assertSame(address1, result.get(1).getAddresses().get(0));

		assertSame(user, result.get(2).getUser());
		assertEquals(1, result.get(2).getAddresses().size());
		assertSame(address2, result.get(2).getAddresses().get(0));
	}

	// helpers

	private User user() {
		return new User(1L, "f", "l", "u", "@");
	}

	private Address address() {
		return new Address(1L, "f", "l", "c", "a1", "a2", "a3", "1000", "city", "AT", "+43");
	}
}
