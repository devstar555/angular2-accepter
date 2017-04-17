package model.platformaccountgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class PlatformAccountGroupRepositoryTest {

	protected abstract PlatformAccountGroupRepository newRepository();

	protected PlatformAccountGroupRepository repository;

	private static void resetModelFactories() {
		PlatformAccountGroupRepositoryFactory.FACTORY = PlatformAccountGroupRepositoryFactory
				.factoryCaching(PlatformAccountGroupRepositoryFactory.factoryMemory());
	}

	@Before
	public void beforeRepositoryTest() {
		resetModelFactories();
		this.repository = newRepository();
	}

	@After
	public void afterRepositoryTest() {
		this.repository = null;
		resetModelFactories();
	}

	@Test
	public void save_platformAccountGroup() {
		PlatformAccountGroup request = getPlatformAccountsGroupData();
		PlatformAccountGroup group = repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());
		assertTrue(group.getId() != null);
		assertEquals(request.getName(), group.getName());
		assertEquals(request.getDescription(), group.getDescription());
		assertEquals(1, request.getAccounts().length);
		assertEquals(request.getAccounts()[0], group.getAccounts()[0]);
	}
	
	@Test
	public void getplatformAccountGroup() {
		PlatformAccountGroup request = getPlatformAccountsGroupData();
		PlatformAccountGroup group = repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());

		PlatformAccountGroup actual = repository.findPlatformAccountGroup(group.getId());

		assertTrue(actual.getId() != null);
		assertEquals(group.getId(), actual.getId());
		assertEquals(group.getName(), actual.getName());
		assertEquals(group.getDescription(), actual.getDescription());
		assertEquals(1, request.getAccounts().length);
		assertEquals(group.getAccounts()[0], actual.getAccounts()[0]);

		// try wrong Id
		actual = repository.findPlatformAccountGroup(Long.MAX_VALUE);
		assertNull(actual);
	}

	@Test
	public void save_platformAccountGroupWithManyAccountsData() {

		List<String> accounts = new ArrayList<>();

		int accountsCount = 0;
		while (accountsCount < 29) {
			accounts.add("somebody" + accountsCount + "@amazon.marketplace.com");
			accountsCount++;
		}


		PlatformAccountGroup request = getPlatformAccountsGroupData();
		request.setAccounts(accounts.stream().toArray(size -> new String[size]));
		PlatformAccountGroup group = repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());
		assertTrue(group.getId() != null);
		assertEquals(request.getName(), group.getName());
		assertEquals(request.getDescription(), group.getDescription());
		assertEquals(29, request.getAccounts().length);
		accountsCount = 0;
		while (accountsCount < 29) {
			assertEquals(request.getAccounts()[accountsCount], group.getAccounts()[accountsCount]);
			accountsCount++;
		}

	}

	@Test
	public void saveAndFind_platformAccountGroup() {
		PlatformAccountGroup request = getPlatformAccountsGroupData();
		PlatformAccountGroup expected = repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());
		List<PlatformAccountGroup> result = repository.findAllPlatformAccountGroups();
		assertEquals(1, result.size());
		assertEquals(expected.getId(), result.get(0).getId());
		assertEquals(expected.getName(), result.get(0).getName());
		assertEquals(expected.getDescription(), result.get(0).getDescription());
		assertEquals("acct1", expected.getAccounts()[0]);
	}

	@Test
	public void saveAndUpdate_platformAccountGroup() {
		String[] accounts = { "acct2" };
		PlatformAccountGroup request = getPlatformAccountsGroupData();
		PlatformAccountGroup group = repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());
		PlatformAccountGroup actual = repository.updatePlatformGroup(group.getId(), "usPlatformGrp", "Done", accounts);
		assertEquals(group.getId(), actual.getId());
		assertEquals("usPlatformGrp", actual.getName());
		assertEquals("Done", actual.getDescription());
		assertEquals(accounts[0], actual.getAccounts()[0]);

	}

	@Test
	public void saveAndDeleteFind_platformAccountGroup() {
		PlatformAccountGroup request = getPlatformAccountsGroupData();
		PlatformAccountGroup group = repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());
		repository.deletePlatformAccountGroup(group.getId());
		List<PlatformAccountGroup> result = repository.findAllPlatformAccountGroups();
		assertEquals(0, result.size());
	}

	private PlatformAccountGroup getPlatformAccountsGroupData() {
		PlatformAccountGroup request = new DatabasePlatformAccountGroup();
		request.setName("ukPlatformGrp");
		request.setDescription("ok");
		request.setAccounts(new String[] { "acct1" });
		return request;
	}

	@Test(expected = IllegalArgumentException.class)
	public void savePlatformAccountGroupWithAccountHavingComma() {
		PlatformAccountGroup request = getPlatformAccountsGroupData();
		request.setAccounts(new String[] { "Wrong,Account" });
		repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());
	}

	@Test(expected = IllegalArgumentException.class)
	public void updatePlatformAccountGroupWithAccountHavingComma() {
		PlatformAccountGroup request = getPlatformAccountsGroupData();
		PlatformAccountGroup group = repository.savePlatformAccountGroup(request.getName(), request.getDescription(),
				request.getAccounts());

		request.setAccounts(new String[] { "Wrong,Account" });

		repository.updatePlatformGroup(group.getId(), request.getName(), request.getDescription(),
				request.getAccounts());
	}

}