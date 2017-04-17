package model.time;

import java.util.Date;

class TimeProviderOffset implements TimeProvider {

	private final long offsetMillis;

	TimeProviderOffset(long offsetMillis) {
		this.offsetMillis = offsetMillis;
	}

	@Override
	public Date getCurrentDate() {
		return new Date(new Date().getTime() + offsetMillis);
	}

}
