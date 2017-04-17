package model.time;

import java.util.Date;

public class TimeProviderFixed implements TimeProvider {

	private final Date date;

	public TimeProviderFixed(Date date) {
		this.date = date;
	}

	@Override
	public Date getCurrentDate() {
		return date;
	}

}
