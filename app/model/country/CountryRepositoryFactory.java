package model.country;

import java.util.function.Supplier;

public final class CountryRepositoryFactory {

	public static Supplier<CountryRepository> factoryTextFile() {
		return () -> new TextFileCountryRepository();
	}

	public static Supplier<CountryRepository> factoryCaching(final Supplier<CountryRepository> factory) {
		return new Supplier<CountryRepository>() {
			private CountryRepository cached;

			@Override
			public synchronized CountryRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static Supplier<CountryRepository> FACTORY = factoryCaching(factoryTextFile());

	public static CountryRepository get() {
		return FACTORY.get();
	}

	private CountryRepositoryFactory() {

	}

}
