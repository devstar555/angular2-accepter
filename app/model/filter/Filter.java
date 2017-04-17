package model.filter;

import java.util.List;

import model.order.Bundle;

/**
 * <p>
 * A filter does or does not match a {@link Bundle}.
 * </p>
 */
public interface Filter {
	Long getId();

	/**
	 * Checks whether or not this filter matches the bundle.
	 * 
	 * @param bundle
	 *            The bundle to check for matches
	 * @return An empty list if this filter does not match the bundle or a list
	 *         of {@link Match}es otherwise.
	 */
	List<Match> match(Bundle bundle);
}
