package model.config;

public class Config {

	public static long getCleanData() {
		return Long.valueOf(ConfigRepositoryFactory.get().getValue(DefinedProperty.CLEAN_DATA.getName()));
	}

	public static void setCleanData(long value) {
		ConfigRepositoryFactory.get().setValue(DefinedProperty.CLEAN_DATA.getName(), String.valueOf(value));
	}

	public static int getZipMaxDistance() {
		return Integer.valueOf(ConfigRepositoryFactory.get().getValue(DefinedProperty.ZIP_MAX_DISTANCE.getName()));
	}

	public static void setZipMaxDistance(int value) {
		ConfigRepositoryFactory.get().setValue(DefinedProperty.ZIP_MAX_DISTANCE.getName(), String.valueOf(value));
	}
}
