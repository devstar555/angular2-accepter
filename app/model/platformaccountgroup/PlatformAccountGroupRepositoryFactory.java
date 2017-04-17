package model.platformaccountgroup;

import java.util.function.Supplier;

public final class PlatformAccountGroupRepositoryFactory {

	public static Supplier<PlatformAccountGroupRepository> factoryDatabase() {
		return () -> new DatabasePlatformAccountGroupRepository();
	}

	public static Supplier<PlatformAccountGroupRepository> factoryMemory() {
		return () -> new MemoryPlatformAccountGroupRepository();
	}

	public static Supplier<PlatformAccountGroupRepository> factoryCaching(
			final Supplier<PlatformAccountGroupRepository> factory) {
		return new Supplier<PlatformAccountGroupRepository>() {
			private PlatformAccountGroupRepository cached;

			@Override
			public synchronized PlatformAccountGroupRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static Supplier<PlatformAccountGroupRepository> FACTORY = factoryDatabase();

	public static PlatformAccountGroupRepository get() {
		return FACTORY.get();
	}

	private PlatformAccountGroupRepositoryFactory() {

	}

}
