package model.filter;

import java.util.function.Supplier;

public class FilterRepositoryFactory {

	public static Supplier<FilterRepository> factoryMemory() {
		return () -> new MemoryFilterRepository();
	}

	public static Supplier<FilterRepository> factoryDatabase() {
		return () -> new DatabaseFilterRepository();
	}

	public static Supplier<FilterRepository> factoryCaching(final Supplier<FilterRepository> factory) {
		return new Supplier<FilterRepository>() {
			private FilterRepository cached;

			@Override
			public synchronized FilterRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static Supplier<FilterRepository> FACTORY = factoryDatabase();

	public static FilterRepository get() {
		return FACTORY.get();
	}

	private FilterRepositoryFactory() {

	}

}
