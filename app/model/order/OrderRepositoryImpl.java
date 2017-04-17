package model.order;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

class OrderRepositoryImpl implements OrderRepository {

	@Override
	public Address newAddress(Long externalId, String firstname, String lastname, String company, String address1,
			String address2, String address3, String zip, String city, String country, String phone) {
		if (externalId == null)
			throw new IllegalArgumentException("externalId is required");

		firstname = StringUtils.trimToNull(firstname);
		lastname = StringUtils.trimToNull(lastname);
		company = StringUtils.trimToNull(company);
		address1 = StringUtils.trimToNull(address1);
		address2 = StringUtils.trimToNull(address2);
		address3 = StringUtils.trimToNull(address3);
		city = StringUtils.trimToNull(city);
		zip = StringUtils.trimToNull(zip);
		phone = StringUtils.trimToNull(phone);

		country = StringUtils.trimToNull(country);
		if (country != null)
			country = country.toUpperCase();

		return new Address(externalId, firstname, lastname, company, address1, address2, address3, zip, city, country,
				phone);
	}

	@Override
	public Bundle newBundle(User user, List<Address> addresses, String platformAccountId) {
		if (user == null)
			throw new IllegalArgumentException("user is required");
		if (addresses == null)
			addresses = Collections.emptyList();

		addresses = addresses.stream().filter(x -> x != null).collect(Collectors.toList());
		return new Bundle(user, addresses, platformAccountId);
	}

	@Override
	public User newUser(Long externalId, String firstname, String lastname, String username, String mail) {
		if (externalId == null)
			throw new IllegalArgumentException("externalId is required");

		firstname = StringUtils.trimToNull(firstname);
		lastname = StringUtils.trimToNull(lastname);
		username = StringUtils.trimToNull(username);
		mail = StringUtils.trimToNull(mail);

		return new User(externalId, firstname, lastname, username, mail);
	}

}
