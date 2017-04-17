package model.time;

import java.util.Date;
import java.util.function.Supplier;

public class TimeProviderFactory {

	public static Supplier<TimeProvider> factoryOffset(long offsetMillis) {
		return () -> new TimeProviderOffset(offsetMillis);
	}

	public static Supplier<TimeProvider> factoryFixed(Date date) {
		return () -> new TimeProviderFixed(date);
	}

	public static Supplier<TimeProvider> FACTORY = factoryOffset(0L);

	public static TimeProvider get() {
		return FACTORY.get();
	}

	private TimeProviderFactory() {

	}

}
