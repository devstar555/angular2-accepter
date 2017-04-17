package model.platformaccountgroup;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Validations and formatting for input
 * </p>
 * <p>
 * The idea of this class is to centralize as much as possible the most common
 * validations and formatting of fields
 * </p>
 */
class Validator {

	static String validateAndFormatName(String name) {

		if (StringUtils.isBlank(name))
			throw new IllegalArgumentException("the name is required");

		if (!name.matches("^[a-zA-Z0-9_.-]*$"))
			throw new IllegalArgumentException("name may only contain alphabetic and numeric characters");

		return name;
	}

	static String formatDescription(String description) {
		if (description != null)
			return StringUtils.trimToNull(description);

		return description;
	}

	public static String[] validateAndFormatAccounts(String[] accounts) {
		if (accounts == null)
			throw new IllegalArgumentException("the accounts is required");
		for (String account : accounts) {
			if (!account.matches("^[a-zA-Z0-9_@.-]*$"))
				throw new IllegalArgumentException("Account may only contain alphabetic, numeric or one of the following characters: _@.-");
		}

		return accounts;
	}
}
