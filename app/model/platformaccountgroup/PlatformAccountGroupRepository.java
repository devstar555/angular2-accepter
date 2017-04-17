package model.platformaccountgroup;

import java.util.List;

public interface PlatformAccountGroupRepository {

	List<PlatformAccountGroup> findAllPlatformAccountGroups();

	void deletePlatformAccountGroup(Long id);

	PlatformAccountGroup savePlatformAccountGroup(String name, String description, String[] accounts);

	PlatformAccountGroup updatePlatformGroup(Long id, String name, String description, String[] accounts);

	// Gets from Cache
	PlatformAccountGroup findById(Long id);

	PlatformAccountGroup findPlatformAccountGroup(Long id);
}
