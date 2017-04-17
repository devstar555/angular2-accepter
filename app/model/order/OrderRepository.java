package model.order;

import java.util.List;

public interface OrderRepository {
	User newUser(Long externalId, String firstname, String lastname, String username, String mail);
	Address newAddress(Long externalId, String firstname, String lastname, String company, String address1,
			String address2, String address3, String zip, String city, String country, String phone);

	Bundle newBundle(User user, List<Address> addresses, String platformAccountId);

}
