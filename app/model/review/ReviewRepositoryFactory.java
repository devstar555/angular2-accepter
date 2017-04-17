package model.review;

import java.util.function.Supplier;

public final class ReviewRepositoryFactory {

	public static Supplier<ReviewRepository> factoryMemory() {
		return () -> new MemoryReviewRepository();
	}

	public static Supplier<ReviewRepository> factoryDatabase() {
		return () -> new DatabaseReviewRepository();
	}

	public static Supplier<ReviewRepository> factoryCaching(final Supplier<ReviewRepository> factory) {
		return new Supplier<ReviewRepository>() {
			private ReviewRepository cached;

			@Override
			public synchronized ReviewRepository get() {
				if (cached == null)
					cached = factory.get();

				return cached;
			}
		};
	}

	public static Supplier<ReviewRepository> FACTORY = factoryDatabase();

	public static ReviewRepository get() {
		return FACTORY.get();
	}

	private ReviewRepositoryFactory() {

	}

}
