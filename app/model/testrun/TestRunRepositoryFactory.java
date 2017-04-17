package model.testrun;

import java.util.function.Supplier;

public final class TestRunRepositoryFactory {

	public static Supplier<TestRunRepository> factoryMemory() {
		return () -> new MemoryTestRunRepository();
	}

	public static Supplier<TestRunRepository> factoryDatabase() {
		return () -> new DatabaseTestRunRepository();
	}

	public static Supplier<TestRunRepository> factoryCaching(final Supplier<TestRunRepository> factory) {
		return new Supplier<TestRunRepository>() {
			private TestRunRepository cached;

			@Override
			public synchronized TestRunRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static Supplier<TestRunRepository> FACTORY = factoryDatabase();

	public static TestRunRepository get() {
		return FACTORY.get();
	}

	private TestRunRepositoryFactory() {

	}

}
