package model.time;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import model.time.TimeProviderFactory;

public class TimeProviderFixedTest {

	@Test
	public void simple() {
		final Date date = new Date();
		assertEquals(date, TimeProviderFactory.factoryFixed(date).get().getCurrentDate());
	}

}
