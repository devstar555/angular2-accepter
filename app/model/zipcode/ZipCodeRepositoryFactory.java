package model.zipcode;

import java.util.function.Supplier;

public final class ZipCodeRepositoryFactory {

	public static Supplier<ZipCodeRepository> FACTORY = factoryDatabase();

	private ZipCodeRepositoryFactory() {
	}

	public static Supplier<ZipCodeRepository> factoryDatabase() {
		return DatabaseZipCodeRepository::new;
	}

	public static Supplier<ZipCodeRepository> factoryMemory() {
		return MemoryZipCodeRepository::new;
	}

	public static Supplier<ZipCodeRepository> factoryCaching(final Supplier<ZipCodeRepository> factory) {
		return new Supplier<ZipCodeRepository>() {
			private ZipCodeRepository cached;

			@Override
			public synchronized ZipCodeRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static ZipCodeRepository get() {
		return FACTORY.get();
	}
}
