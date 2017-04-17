package model.platformaccountgroup;

public class TestPlatformAccountGroupCreator {

	public static PlatformAccountGroup createPlatformAccountGroup() {
		return PlatformAccountGroupRepositoryFactory.get().savePlatformAccountGroup("test", "desc",
				new String[] { "amazon.uk@dodax.com" });
	}
}
