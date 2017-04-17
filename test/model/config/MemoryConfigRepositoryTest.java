package model.config;

public class MemoryConfigRepositoryTest extends ConfigRepositoryTest {

	@Override
	protected ConfigRepository newRepository() {
		return new MemoryConfigRepository();
	}

}
