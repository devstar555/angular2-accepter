package model.config;

import java.util.function.Supplier;

public final class ConfigRepositoryFactory {

	public static Supplier<ConfigRepository> factoryDatabase() {
		return () -> new DatabaseConfigRepository();
	}

	public static Supplier<ConfigRepository> factoryMemory() {
		return () -> new MemoryConfigRepository();
	}

	public static Supplier<ConfigRepository> factoryCaching(final Supplier<ConfigRepository> factory) {
		return new Supplier<ConfigRepository>() {
			private ConfigRepository cached;

			@Override
			public synchronized ConfigRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static Supplier<ConfigRepository> FACTORY = factoryDatabase();

	public static ConfigRepository get() {
		return FACTORY.get();
	}

	private ConfigRepositoryFactory() {

	}

}
