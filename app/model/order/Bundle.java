package model.order;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bundle {

	private final User user;
	private final List<Address> addresses;
	private final String platformAccountId;

	Bundle(User user, List<Address> addresses, String platformAccountId) {
		this.user = user;
		this.addresses = addresses;
		this.platformAccountId = platformAccountId;
	}

	public List<Bundle> isolateAddresses() {
		final List<Bundle> result = new ArrayList<>(addresses.size() + 1);

		result.add(new Bundle(user, Collections.emptyList(), platformAccountId));
		for (Address cur : addresses)
			result.add(new Bundle(user, Arrays.asList(cur), platformAccountId));

		return result;
	}

	public User getUser() {
		return user;
	}

	public List<Address> getAddresses() {
		return addresses;
	}

	public String getPlatformAccountId() {
		return platformAccountId;
	}
}
