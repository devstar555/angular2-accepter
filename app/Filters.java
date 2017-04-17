import javax.inject.Inject;

import play.filters.cors.CORSFilter;
import play.filters.gzip.GzipFilter;
import play.http.DefaultHttpFilters;

public class Filters extends DefaultHttpFilters {
	@Inject
	public Filters(CORSFilter corsFilter, GzipFilter gzipFilter) {
		super(corsFilter, gzipFilter);
	}
}