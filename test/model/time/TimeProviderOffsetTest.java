package model.time;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import model.time.TimeProviderFactory;

public class TimeProviderOffsetTest {

	@Test
	public void currentTime() {
		final Date dateBefore = new Date();
		final Date dateProvider = TimeProviderFactory.factoryOffset(0).get().getCurrentDate();
		final Date dateAfter = new Date();
		Assert.assertFalse(dateBefore.after(dateProvider));
		Assert.assertFalse(dateAfter.before(dateProvider));
	}

	@Test
	public void futureOffset() {
		final long offset = 1000L * 3600;
		final Date dateBefore = new Date();
		final Date dateProvider = TimeProviderFactory.factoryOffset(offset).get().getCurrentDate();
		final Date dateClean = new Date(dateProvider.getTime() - offset);
		final Date dateAfter = new Date();

		Assert.assertFalse(dateBefore.after(dateClean));
		Assert.assertFalse(dateAfter.before(dateClean));
		Assert.assertTrue(dateProvider.after(new Date()));
	}

	@Test
	public void pastOffset() {
		final long offset = 1000L * 3600;
		final Date dateBefore = new Date();
		final Date dateProvider = TimeProviderFactory.factoryOffset(-1 * offset).get().getCurrentDate();
		final Date dateClean = new Date(dateProvider.getTime() + offset);
		final Date dateAfter = new Date();

		Assert.assertFalse(dateBefore.after(dateClean));
		Assert.assertFalse(dateAfter.before(dateClean));
		Assert.assertTrue(dateProvider.before(new Date(new Date().getTime() - 1000L * 60)));
	}

}
