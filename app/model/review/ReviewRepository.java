package model.review;

import java.util.List;

public interface ReviewRepository {
	ReviewRequest saveReviewRequest(Long orderId, List<ReviewReason> reasons);
	ReviewResult saveReviewResult(Long orderId, String action);
	List<ReviewRequest> findAllReviewRequests();
	List<ReviewResult> findAllReviewResults();
	ReviewReason newReviewReason(String type, String value);
	List<ReviewRequest> findReviewRequestsTimeFilter(long seconds);
	List<ReviewResult> findReviewResultsTimeFilter(long seconds);
	void cleanReviewsOlderThan(long seconds);
}
