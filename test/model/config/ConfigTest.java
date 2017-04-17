package model.config;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ConfigTest {

	private static void resetModelFactories() {
		ConfigRepositoryFactory.FACTORY = ConfigRepositoryFactory
				.factoryCaching(ConfigRepositoryFactory.factoryMemory());
	}

	@Before
	public void before() {
		resetModelFactories();
	}

	@After
	public void after() {
		resetModelFactories();
	}

	@Test
	public void cleanData() {
		long defaultValue = Long.valueOf(DefinedProperty.CLEAN_DATA.getDefaultValue());
		assertEquals(defaultValue, Config.getCleanData());
		Config.setCleanData(500L);
		assertEquals(500L, Config.getCleanData());
	}

	@Test
	public void zipMaxDistanceData() {
		int defaultValue = Integer.valueOf(DefinedProperty.ZIP_MAX_DISTANCE.getDefaultValue());
		assertEquals(defaultValue, Config.getZipMaxDistance());
		Config.setZipMaxDistance(1200);
		assertEquals(1200, Config.getZipMaxDistance());
	}

}
