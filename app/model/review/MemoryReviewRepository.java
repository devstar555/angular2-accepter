package model.review;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import model.config.Config;
import model.time.TimeProviderFactory;
import util.Utils;

class MemoryReviewRepository implements ReviewRepository {

	private final ArrayList<ReviewRequest> reviewRequestLst = new ArrayList<ReviewRequest>();
	private final ArrayList<ReviewResult> reviewResultLst = new ArrayList<ReviewResult>();

	private long idReviewRequest = 0L;
	private long idReviewResult = 0L;
	private long idReviewReason = 0L;

	@Override
	public ReviewRequest saveReviewRequest(Long orderId, List<ReviewReason> reasons) {
		if (orderId == null || reasons == null || reasons.isEmpty())
			throw new IllegalArgumentException();

		for (ReviewRequest cur : reviewRequestLst) {
			if (cur.getOrderId().equals(orderId)) {
				reviewRequestLst.remove(cur);
				break;
			}
		}

		final ReviewRequest request = new MemoryReviewRequest(idReviewRequest++, orderId, currentDate(),
				Utils.removeDuplicates(reasons, ReviewReason::compareValues));
		reviewRequestLst.add(request);

		return request;
	}

	@Override
	public ReviewResult saveReviewResult(Long orderId, String action) {
		if ((orderId == null) || StringUtils.isBlank(action))
			throw new IllegalArgumentException();

		for (ReviewResult cur : reviewResultLst) {
			if (cur.getOrderId().equals(orderId)) {
				reviewResultLst.remove(cur);
				break;
			}
		}

		final ReviewResult result = new MemoryReviewResult(idReviewResult++, orderId, StringUtils.trimToNull(action),
				currentDate());
		reviewResultLst.add(result);
		return result;
	}

	@Override
	public List<ReviewRequest> findAllReviewRequests() {
		return new ArrayList<>(reviewRequestLst);
	}

	@Override
	public List<ReviewResult> findAllReviewResults() {
		return new ArrayList<>(reviewResultLst);
	}

	@Override
	public ReviewReason newReviewReason(String type, String value) {
		if (type == null || StringUtils.isBlank(type))
			throw new IllegalArgumentException();

		return new MemoryReviewReason(idReviewReason++, StringUtils.trimToNull(type), StringUtils.trimToNull(value));
	}

	@Override
	public List<ReviewRequest> findReviewRequestsTimeFilter(long seconds) {
		ArrayList<ReviewRequest> reviewRequestTimeFilterLst = new ArrayList<ReviewRequest>();
		for (ReviewRequest cur : reviewRequestLst) {
			if (cur.getRequestDate().after(Utils.getDateBefore(seconds))) {
				reviewRequestTimeFilterLst.add(cur);
			}
		}
		return reviewRequestTimeFilterLst;
	}

	@Override
	public List<ReviewResult> findReviewResultsTimeFilter(long seconds) {
		ArrayList<ReviewResult> reviewResultTimeFilterLst = new ArrayList<ReviewResult>();
		for (ReviewResult cur : reviewResultLst) {
			if (cur.getDateAction().after(Utils.getDateBefore(seconds))) {
				reviewResultTimeFilterLst.add(cur);
			}
		}
		return reviewResultTimeFilterLst;
	}

	@Override
	public void cleanReviewsOlderThan(long seconds) {
		if (seconds < Config.getCleanData())
			throw new IllegalArgumentException(
					seconds + " seconds is below allowed minimum of " + Config.getCleanData());

		Date forComparison = Utils.getDateBefore(seconds);
		// first delete old requests with the corresponding results
		List<ReviewRequest> oldRequests = reviewRequestLst.stream()
				.filter(req -> req.getRequestDate().before(forComparison)).collect(Collectors.toList());

		for (ReviewRequest req : oldRequests) {
			reviewResultLst.removeIf(res -> res.getOrderId().equals(req.getOrderId()));
			reviewRequestLst.remove(req);
		}
		// then delete old results
		reviewResultLst.removeIf(res -> res.getDateAction().before(forComparison));

	}

	private Date currentDate() {
		return TimeProviderFactory.get().getCurrentDate();
	}

}
