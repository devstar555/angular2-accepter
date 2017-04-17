package model.review;

import static model.review.ReviewReason.compareValues;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import model.review.ReviewReason;

public class ReviewReasonTest {

	@Test
	public void testCompareValues() {
		assertTrue(0 == compareValues(null, null));
		assertTrue(0 < compareValues(r(null, null), null));
		assertTrue(0 > compareValues(null, r(null, null)));

		assertTrue(0 == compareValues(r("t", "v"), r("t", "v")));
		assertTrue(0 == compareValues(r("t", null), r("t", null)));
		assertTrue(0 == compareValues(r(null, "v"), r(null, "v")));
		assertTrue(0 == compareValues(r(null, null), r(null, null)));

		assertTrue(0 < compareValues(r("t", "v"), r("t", null)));
		assertTrue(0 > compareValues(r("t", null), r("t", "v")));
		assertTrue(0 > compareValues(r(null, "v"), r("t", "v")));
		assertTrue(0 < compareValues(r("t", "v"), r(null, "v")));

		assertTrue(0 < compareValues(r("t", "v"), r("t", "a")));
		assertTrue(0 < compareValues(r("t", "v"), r("a", "v")));
	}

	private ReviewReason r(String type, String value) {
		return new ReviewReason(type, value) {
			@Override
			public Long getId() {
				return null;
			}
		};
	}

}
