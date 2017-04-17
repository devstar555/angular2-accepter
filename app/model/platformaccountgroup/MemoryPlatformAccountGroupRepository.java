package model.platformaccountgroup;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

class MemoryPlatformAccountGroupRepository implements PlatformAccountGroupRepository {

	private List<PlatformAccountGroup> values = new ArrayList<>();
	private final AtomicLong platformAccountGroupCounter = new AtomicLong(1);

	@Override
	public List<PlatformAccountGroup> findAllPlatformAccountGroups() {
		return values;
	}

	@Override
	public void deletePlatformAccountGroup(Long id) {
		values = values.stream().filter(e -> !e.getId().equals(id)).collect(Collectors.toList());

	}

	@Override
	public PlatformAccountGroup updatePlatformGroup(Long id, String name, String description, String[] accounts) {

		String validatedName = Validator.validateAndFormatName(name);
		String formattedDescription = Validator.formatDescription(description);
		String[] validatedAccounts = Validator.validateAndFormatAccounts(accounts);

		AtomicInteger count = new AtomicInteger(0);
		List<PlatformAccountGroup> newValues = values;
		new ArrayList<>(newValues).stream().forEach(e -> {
			if (e.getId().equals(id)) {
				e.setName(validatedName);
				e.setDescription(formattedDescription);
				e.setAccounts(validatedAccounts);
				count.incrementAndGet();
			}
		});

		if (count.get() == 0) {
			throw new IllegalArgumentException();
		}

		values = newValues;

		return values.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);

	}

	@Override
	public PlatformAccountGroup savePlatformAccountGroup(String name, String description, String[] accounts) {
		values.stream().forEach(e -> {
			if (e.getName().equals(name)) {
				throw new IllegalArgumentException();
			}
		});

		String validatedName = Validator.validateAndFormatName(name);
		String formattedDescription = Validator.formatDescription(description);
		String[] validatedAccounts = Validator.validateAndFormatAccounts(accounts);

		PlatformAccountGroup group = new MemoryPlatformAccountGroup(platformAccountGroupCounter.incrementAndGet(),
				validatedName, formattedDescription, validatedAccounts);
		values.add(group);
		return group;
	}

	@Override
	public PlatformAccountGroup findById(Long id) {
		return values.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	@Override
	public PlatformAccountGroup findPlatformAccountGroup(Long id) {
		return findById(id);
	}
}
