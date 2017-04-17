package model.platformaccountgroup;

public class MemoryPlatformAccountGroupRepositoryTest extends PlatformAccountGroupRepositoryTest {

	@Override
	protected PlatformAccountGroupRepository newRepository() {
		return new MemoryPlatformAccountGroupRepository();
	}

}