package model.country;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

class TextFileCountryRepository implements CountryRepository {

	private static final String FILE_NAME = "conf/countries.csv";
	private static final String SEPARATOR = ":";
	private final AtomicLong countryNumId = new AtomicLong(1);
	private static TreeMap<String, Country> mapOfCountriesByCode;
	private static TreeMap<String, Country> mapOfCountriesByName;

	public TextFileCountryRepository() {
		initializeCountriesInMemory();
	}

	@Override
	public List<Country> getCountries() {
		return new ArrayList<Country>(mapOfCountriesByName.values());
	}

	private void initializeCountriesInMemory() {

		List<String> lines = new ArrayList<String>();
		try {
			lines = Files.readLines(new File(FILE_NAME), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open countries file", e);
		}

		mapOfCountriesByCode = new TreeMap<>();
		mapOfCountriesByName = new TreeMap<>();

		for (String line : lines) {
			String[] tokens = line.split(SEPARATOR);
			Country toAdd = new TextFileCountry(countryNumId.getAndIncrement(), tokens[1], tokens[0]);
			mapOfCountriesByCode.put(tokens[1], toAdd);
			mapOfCountriesByName.put(tokens[0], toAdd);
		}
	}

	@Override
	public boolean countryIsValid(String code) {
		return mapOfCountriesByCode.containsKey(code);
	}

}
