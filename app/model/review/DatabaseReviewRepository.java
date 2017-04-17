package model.review;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.avaje.ebean.Query;
import com.avaje.ebean.QueryIterator;

import db.DbReviewReason;
import db.DbReviewRequest;
import db.DbReviewResult;
import model.config.Config;
import model.time.TimeProviderFactory;
import play.db.ebean.Transactional;
import util.Utils;

class DatabaseReviewRepository implements ReviewRepository {

	@Override
	public ReviewRequest saveReviewRequest(Long orderId, List<ReviewReason> reasons) {
		if (orderId == null)
			throw new IllegalArgumentException("orderId is required");
		if (reasons == null || reasons.size() <= 0)
			throw new IllegalArgumentException("reasons are required");

		// prepare database entries for reasons
		final List<DbReviewReason> dbReasons = new ArrayList<>(reasons.size());
		for (ReviewReason curReason : Utils.removeDuplicates(reasons, ReviewReason::compareValues)) {

			// lookup existing reason
			DbReviewReason tmp = DbReviewReason.FINDER.where().eq(DbReviewReason.COLUMN_TYPE, curReason.getType())
					.eq(DbReviewReason.COLUMN_VALUE, curReason.getValue()).findUnique();

			// create new reason if it does not exist yet
			if (tmp == null) {
				tmp = new DbReviewReason();
				tmp.setType(curReason.getType());
				tmp.setValue(curReason.getValue());
				tmp.save();
			}

			dbReasons.add(tmp);
		}

		// lookup existing request
		DbReviewRequest request = DbReviewRequest.FINDER.where().eq(DbReviewRequest.COLUMN_ORDERID, orderId)
				.findUnique();

		// create new request if it does not exist yet
		if (request == null)
			request = new DbReviewRequest();

		request.setOrderId(orderId);
		request.setRequestDate(currentDate());
		request.setReasons(dbReasons);
		request.save();

		return new DatabaseReviewRequest(request);
	}

	@Override
	public ReviewResult saveReviewResult(Long orderId, String action) {
		if (orderId == null)
			throw new IllegalArgumentException("orderId is required");
		if (StringUtils.isBlank(action))
			throw new IllegalArgumentException("action is required");

		DbReviewResult result = DbReviewResult.FINDER.where().eq(DbReviewResult.COLUMN_ORDERID, orderId).findUnique();
		if (result == null)
			result = new DbReviewResult();

		result.setOrderId(orderId);
		result.setAction(StringUtils.trimToNull(action));
		result.setActionDate(currentDate());
		result.save();

		return new DatabaseReviewResult(result);
	}

	@Override
	public List<ReviewRequest> findAllReviewRequests() {
		final Query<DbReviewRequest> query = DbReviewRequest.FINDER.fetch(DbReviewRequest.MANY_TO_MANY_REASONS, "*");
		return Utils.mapList(query.orderBy("").findList(), x -> new DatabaseReviewRequest(x));
	}

	@Override
	public List<ReviewResult> findAllReviewResults() {
		return Utils.mapList(DbReviewResult.FINDER.all(), x -> new DatabaseReviewResult(x));
	}

	@Override
	public ReviewReason newReviewReason(String type, String value) {
		if (StringUtils.isBlank(type))
			throw new IllegalArgumentException("type is required");

		return new DatabaseReviewReason(StringUtils.trimToNull(type), StringUtils.trimToNull(value));
	}

	@Override
	public List<ReviewRequest> findReviewRequestsTimeFilter(long seconds) {
		final Query<DbReviewRequest> query = DbReviewRequest.FINDER.fetch(DbReviewRequest.MANY_TO_MANY_REASONS, "*");
		return Utils.mapList(query.where().gt(DbReviewRequest.REQUEST_DATE, Utils.getDateBefore(seconds)).findList(),
				x -> new DatabaseReviewRequest(x));
	}

	@Override
	public List<ReviewResult> findReviewResultsTimeFilter(long seconds) {
		final Query<DbReviewResult> query = DbReviewResult.FINDER.where()
				.gt(DbReviewResult.COLUMN_ACTION_DATE, Utils.getDateBefore(seconds)).query();
		return Utils.mapList(query.findList(), x -> new DatabaseReviewResult(x));
	}

	@Override
	@Transactional
	public void cleanReviewsOlderThan(long seconds) {
		if (seconds < Config.getCleanData())
			throw new IllegalArgumentException(
					seconds + " seconds is below allowed minimum of " + Config.getCleanData());

		QueryIterator<DbReviewRequest> it = null;
		Date forComparison = Utils.getDateBefore(seconds);

		try {
			it = DbReviewRequest.FINDER.fetch(DbReviewRequest.MANY_TO_MANY_REASONS, "*").where()
					.lt(DbReviewRequest.COLUMN_REQUEST_DATE, forComparison).findIterate();

			while (it.hasNext()) {
				DbReviewRequest old = it.next();
				// delete results with same order id
				DbReviewResult.FINDER.where().eq(DbReviewResult.COLUMN_ORDERID, old.getOrderId()).delete();
				// delete old request
				old.delete();

				for (DbReviewReason reason : old.getReasons()) {
					if (DbReviewRequest.FINDER.where().eq(DbReviewRequest.MANY_TO_MANY_REASONS, reason).setMaxRows(1)
							.findList().size() > 0)
						continue;

					reason.delete();
				}
			}
		} finally {
			if (it != null)
				it.close();
		}

		// then delete old results
		DbReviewResult.FINDER.where().lt(DbReviewResult.COLUMN_ACTION_DATE, forComparison).delete();

	}

	private Date currentDate() {
		return TimeProviderFactory.get().getCurrentDate();
	}

}
