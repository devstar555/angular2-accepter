package model.testdataset;

import java.util.function.Supplier;

public final class TestDataSetRepositoryFactory {

	public static Supplier<TestDataSetRepository> FACTORY = factoryDatabase();

	private TestDataSetRepositoryFactory() {
	}

	public static Supplier<TestDataSetRepository> factoryDatabase() {
		return DatabaseTestDataSetRepository::new;
	}

	public static Supplier<TestDataSetRepository> factoryMemory() {
		return MemoryTestDataSetRepository::new;
	}

	public static Supplier<TestDataSetRepository> factoryCaching(final Supplier<TestDataSetRepository> factory) {
		return new Supplier<TestDataSetRepository>() {
			private TestDataSetRepository cached;

			@Override
			public synchronized TestDataSetRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static TestDataSetRepository get() {
		return FACTORY.get();
	}
}
