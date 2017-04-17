package model.order;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import model.platformaccountgroup.PlatformAccountGroup;
import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.platformaccountgroup.TestPlatformAccountGroupCreator;

public abstract class OrderRepositoryTest {

	protected abstract OrderRepository newRepository();

	private OrderRepository repository;
	private PlatformAccountGroup group;

	private static void resetModelFactories() {
		PlatformAccountGroupRepositoryFactory.FACTORY = PlatformAccountGroupRepositoryFactory
				.factoryCaching(PlatformAccountGroupRepositoryFactory.factoryMemory());
	}

	@Before
	public void before() {
		resetModelFactories();
		this.repository = newRepository();
		group = TestPlatformAccountGroupCreator.createPlatformAccountGroup();
	}

	@After
	public void after() {
		this.repository = null;
	}

	@Test
	public void bundle_simple() {
		final User user = minimalUser(1L);
		final Address address1 = minimalAddress(2L);
		final Address address2 = minimalAddress(3L);
		final Bundle bundle = repository.newBundle(user, Arrays.asList(address1, address2), group.getAccounts()[0]);
		assertNotNull(bundle);
		assertSame(user, bundle.getUser());
		assertEquals(2, bundle.getAddresses().size());
		assertSame(address1, bundle.getAddresses().get(0));
		assertSame(address2, bundle.getAddresses().get(1));
		assertEquals(group.getAccounts()[0], bundle.getPlatformAccountId());
	}

	@Test
	public void bundle_nullList() {
		final Bundle bundle = repository.newBundle(minimalUser(1L), null, group.getAccounts()[0]);
		assertEquals(0, bundle.getAddresses().size());
	}

	@Test
	public void bundle_nullEntriesList() {
		final Bundle bundle = repository.newBundle(minimalUser(1L), Arrays.asList(null, null), null);
		assertEquals(0, bundle.getAddresses().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void bundle_nullUser() {
		repository.newBundle(null, Arrays.asList(minimalAddress(1L)), null);
	}

	@Test
	public void address_simple() {
		final Address address = repository.newAddress(1L, "f", "l", "c", "a1", "a2", "a3", "12", "v", "de", "+43");
		assertEquals("f", address.getFirstname());
		assertEquals("l", address.getLastname());
		assertEquals("c", address.getCompany());
		assertEquals("a1", address.getAddress1());
		assertEquals("a2", address.getAddress2());
		assertEquals("a3", address.getAddress3());
		assertEquals("12", address.getZip());
		assertEquals("v", address.getCity());
		assertEquals("DE", address.getCountry());
		assertEquals("+43", address.getPhone());
	}

	@Test(expected = IllegalArgumentException.class)
	public void address_nullId() {
		repository.newAddress(null, "f", "l", "c", "a1", "a2", "a3", "12", "v", "de", "+43");
	}

	@Test
	public void address_cleanTrim() {
		final Address address = repository.newAddress(1L, " f\t", "l  ", " c", "\ta1\t\t", " a2 ", "\ta3 ", "  12",
				" v ", "\tde\t", "+43\t");
		assertEquals("f", address.getFirstname());
		assertEquals("l", address.getLastname());
		assertEquals("c", address.getCompany());
		assertEquals("a1", address.getAddress1());
		assertEquals("a2", address.getAddress2());
		assertEquals("a3", address.getAddress3());
		assertEquals("12", address.getZip());
		assertEquals("v", address.getCity());
		assertEquals("DE", address.getCountry());
		assertEquals("+43", address.getPhone());
	}

	@Test
	public void address_cleanBlank() {
		final Address address = repository.newAddress(1L, "", "  ", "\t", "\t ", "\t\t", " \t ", "", " ", " \t", "\t ");
		assertNull(address.getFirstname());
		assertNull(address.getLastname());
		assertNull(address.getCompany());
		assertNull(address.getAddress1());
		assertNull(address.getAddress2());
		assertNull(address.getAddress3());
		assertNull(address.getZip());
		assertNull(address.getCity());
		assertNull(address.getCountry());
		assertNull(address.getPhone());
	}

	@Test
	public void user_simple() {
		final User user = repository.newUser(1L, "f", "l", "u", "m");
		assertEquals((Long) 1L, user.getExternalId());
		assertEquals("f", user.getFirstname());
		assertEquals("l", user.getLastname());
		assertEquals("u", user.getUsername());
		assertEquals("m", user.getMail());
	}

	@Test(expected = IllegalArgumentException.class)
	public void user_nullId() {
		repository.newUser(null, "f", "l", "u", "m");
	}

	@Test
	public void user_cleanTrim() {
		final User user = repository.newUser(1L, "\tf ", " l\t", " u  ", "m ");
		assertEquals((Long) 1L, user.getExternalId());
		assertEquals("f", user.getFirstname());
		assertEquals("l", user.getLastname());
		assertEquals("u", user.getUsername());
		assertEquals("m", user.getMail());
	}

	@Test
	public void user_cleanEmpty() {
		final User user = repository.newUser(1L, "", "", "", "");
		assertEquals((Long) 1L, user.getExternalId());
		assertNull(user.getFirstname());
		assertNull(user.getLastname());
		assertNull(user.getUsername());
		assertNull(user.getMail());
	}

	@Test
	public void user_cleanBlank() {
		final User user = repository.newUser(1L, "  \t ", " ", "", "\t\t");
		assertEquals((Long) 1L, user.getExternalId());
		assertNull(user.getFirstname());
		assertNull(user.getLastname());
		assertNull(user.getUsername());
		assertNull(user.getMail());
	}

	// helpers

	private User minimalUser(Long id) {
		return repository.newUser(id, "f", "l", "u", "m");
	}

	private Address minimalAddress(Long id) {
		return repository.newAddress(2L, "f", "l", "c", "a1", "a2", "a3", "1", "v", "de", "+43");
	}

}
