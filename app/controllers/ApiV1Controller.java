package controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import model.config.ConfigRepository;
import model.config.ConfigRepositoryFactory;
import model.config.Property;
import model.country.Country;
import model.country.CountryRepository;
import model.country.CountryRepositoryFactory;
import model.filter.CompanyFilter;
import model.filter.EmailFilter;
import model.filter.Filter;
import model.filter.FilterHistory;
import model.filter.FilterRepository;
import model.filter.FilterRepositoryFactory;
import model.filter.Match;
import model.filter.PersonFilter;
import model.filter.PhoneFilter;
import model.filter.StreetFilter;
import model.order.Address;
import model.order.Bundle;
import model.order.OrderRepository;
import model.order.OrderRepositoryFactory;
import model.platformaccountgroup.PlatformAccountGroup;
import model.platformaccountgroup.PlatformAccountGroupRepository;
import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.review.ReviewReason;
import model.review.ReviewRepository;
import model.review.ReviewRepositoryFactory;
import model.review.ReviewRequest;
import model.review.ReviewResult;
import model.testdataset.TestDataSet;
import model.testdataset.TestDataSetRepository;
import model.testdataset.TestDataSetRepositoryFactory;
import model.testrun.TestCase;
import model.testrun.TestResult;
import model.testrun.TestRun;
import model.testrun.TestRunRepository;
import model.testrun.TestRunRepositoryFactory;
import model.zipcode.ZipCode;
import model.zipcode.ZipCodeRepository;
import model.zipcode.ZipCodeRepositoryFactory;
import play.db.ebean.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

@Api(value = "API V1")
public class ApiV1Controller extends Controller {

	private static final DecideResult DECIDE_RESULT_ACCEPT;
	private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";
	private static final String DATE_TIMEZONE = "UTC";

	static {
		DECIDE_RESULT_ACCEPT = new DecideResult();
		DECIDE_RESULT_ACCEPT.order = new DecideDecision();
		DECIDE_RESULT_ACCEPT.order.decision = DecideOutcome.ACCEPT;
	}

	private ReviewRepository reviewRepository;
	private FilterRepository filterRepository;
	private OrderRepository orderRepository;
	private TestRunRepository testRunRepository;
	private CountryRepository countryRepository;
	private ConfigRepository configRepository;
	private PlatformAccountGroupRepository platformAccountGroupRepository;
	private ZipCodeRepository zipCodeRepository;
	private TestDataSetRepository testDataSetRepository;

	public ApiV1Controller() {
		this.reviewRepository = ReviewRepositoryFactory.get();
		this.filterRepository = FilterRepositoryFactory.get();
		this.orderRepository = OrderRepositoryFactory.get();
		this.testRunRepository = TestRunRepositoryFactory.get();
		this.countryRepository = CountryRepositoryFactory.get();
		this.configRepository = ConfigRepositoryFactory.get();
		this.platformAccountGroupRepository = PlatformAccountGroupRepositoryFactory.get();
		this.zipCodeRepository = ZipCodeRepositoryFactory.get();
		this.testDataSetRepository = TestDataSetRepositoryFactory.get();
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Ping Response", response = Ping.class) })
	public Result ping() {
		return ok(Json.toJson(new Ping()));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "TestRun saved"),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$TestRunRequest", paramType = "body") })
	public Result testRunSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final TestRunRequest request = Json.fromJson(json, TestRunRequest.class);
		if (request == null)
			return badRequest();
		TestRun toSave = getTestRunFromRequest(request);
		testRunRepository.saveTestRun(toSave);

		return ok();
	}

	private TestRun getTestRunFromRequest(TestRunRequest request) {
		TestRun result = testRunRepository.newTestRun();
		result.setStart(request.start);
		result.setEnd(request.end);

		for (TestCaseRequest req : request.testCases) {
			TestCase toAdd = getTestCaseFromRequest(req);
			result.getTestCases().add(toAdd);
		}

		return result;
	}

	private TestCase getTestCaseFromRequest(TestCaseRequest request) {
		TestCase testCase = testRunRepository.newTestCase();
		testCase.setName(request.name);
		testCase.setActual(request.actual);
		testCase.setExpected(request.expected);
		return testCase;
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Decision", response = DecideResult.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$DecideRequest", paramType = "body") })
	public Result decide() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final DecideRequest request = Json.fromJson(json, DecideRequest.class);
		if (request == null)
			return badRequest();
		if (request.user == null)
			return badRequest();

		final List<Address> addresses = new ArrayList<>();
		if (request.addresses != null)
			for (DecideAddress cur : request.addresses)
				addresses.add(orderRepository.newAddress(cur.id, cur.firstname, cur.lastname, cur.company, cur.address1,
						cur.address2, cur.address3, cur.zip, cur.city, cur.country, cur.phone));

		final Bundle bundle = orderRepository.newBundle(orderRepository.newUser(request.user.id, request.user.firstname,
				request.user.lastname, request.user.username, request.user.mail), addresses, request.platformAccountId);

		final Set<Match> matches = new LinkedHashSet<>();
		for (Filter cur : filterRepository.findAllFilters())
			matches.addAll(cur.match(bundle));

		if (matches.size() <= 0)
			return ok(Json.toJson(DECIDE_RESULT_ACCEPT));

		final DecideResult result = new DecideResult();
		result.order = new DecideDecision();
		result.order.decision = DecideOutcome.REVIEW;
		result.order.reasons = new ArrayList<>();
		for (Match cur : matches) {
			final DecideReason reason = new DecideReason();
			reason.type = cur.getType();
			reason.value = cur.getValue();
			reason.message = cur.getMessage();
			result.order.reasons.add(reason);
		}

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Person Filter Save Response", response = GenericFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$GenericFilterRequest", paramType = "body") })
	public Result personFilterSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final GenericFilterRequest request = Json.fromJson(json, GenericFilterRequest.class);
		if (request == null)
			return badRequest();

		PersonFilter pf = filterRepository.savePersonFilter(request.name, request.country, request.zip,
				request.description,
				request.platformAccountGroupIds);
		GenericFilterResponse result = genericFilterResponse(pf.getId(), pf.getName(), pf.getDescription(),
				pf.getCountry(), pf.getZip(), pf.getPlatformAccountGroupIds());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Company Filter Save Response", response = GenericFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$GenericFilterRequest", paramType = "body") })
	public Result companyFilterSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final GenericFilterRequest request = Json.fromJson(json, GenericFilterRequest.class);
		if (request == null)
			return badRequest();

		CompanyFilter cf = filterRepository.saveCompanyFilter(request.name, request.country, request.zip,
				request.description,
				request.platformAccountGroupIds);
		GenericFilterResponse result = genericFilterResponse(cf.getId(), cf.getName(), cf.getDescription(),
				cf.getCountry(), cf.getZip(), cf.getPlatformAccountGroupIds());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Street Filter Save Response", response = GenericFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$GenericFilterRequest", paramType = "body") })
	public Result streetFilterSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final GenericFilterRequest request = Json.fromJson(json, GenericFilterRequest.class);
		if (request == null)
			return badRequest();

		StreetFilter sf = filterRepository.saveStreetFilter(request.name, request.country, request.zip,
				request.description,
				request.platformAccountGroupIds);
		GenericFilterResponse result = genericFilterResponse(sf.getId(), sf.getName(), sf.getDescription(),
				sf.getCountry(), sf.getZip(), sf.getPlatformAccountGroupIds());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Email Filter Save Response", response = EmailFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$EmailFilterRequest", paramType = "body") })
	public Result emailFilterSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final EmailFilterRequest request = Json.fromJson(json, EmailFilterRequest.class);
		if (request == null)
			return badRequest();

		EmailFilter emailFilter = filterRepository.saveEmailFilter(request.name, request.description,
				request.platformAccountGroupIds);
		return ok(Json.toJson(getEmailFilterResponse(emailFilter)));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Phone Filter Save Response", response = PhoneFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$PhoneFilterRequest", paramType = "body") })
	public Result phoneFilterSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final PhoneFilterRequest request = Json.fromJson(json, PhoneFilterRequest.class);
		if (request == null)
			return badRequest();

		PhoneFilter phoneFilter = filterRepository.savePhoneFilter(request.name, request.description,
				request.platformAccountGroupIds);
		return ok(Json.toJson(getPhoneFilterResponse(phoneFilter)));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Person Filter Update Response", response = GenericFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$PersonFilterUpdateRequest", paramType = "body") })
	public Result personFilterUpdate(Long id) {
		final JsonNode json = request().body().asJson();
		if (json == null || id == null)
			return badRequest();

		final PersonFilterUpdateRequest request = Json.fromJson(json, PersonFilterUpdateRequest.class);
		if (request == null)
			return badRequest();

		PersonFilter pf = filterRepository.updatePersonFilter(id, request.name, request.country, request.zip,
				request.description,
				request.platformAccountGroupIds);
		GenericFilterResponse result = genericFilterResponse(pf.getId(), pf.getName(), pf.getDescription(),
				pf.getCountry(), pf.getZip(), pf.getPlatformAccountGroupIds());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Filter deleted"),
		@ApiResponse(code = 400, message = "Bad Request") })
	public Result personFilterDelete(Long id) {
		if (id == null)
			return badRequest();

		filterRepository.deletePersonFilter(id);
		return ok();
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "TestRuns cleaned") })
	public Result testRunsClean(Long seconds) {
		testRunRepository.cleanTestRunsOlderThan(seconds);
		return ok();
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "ManualReviews cleaned") })
	public Result manualReviewsClean(Long seconds) {
		reviewRepository.cleanReviewsOlderThan(seconds);
		return ok();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Street Filter Updated Response", response = GenericFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$StreetFilterUpdateRequest", paramType = "body") })
	public Result streetFilterUpdate(Long id) {
		final JsonNode json = request().body().asJson();
		if (json == null || id == null)
			return badRequest();

		final StreetFilterUpdateRequest request = Json.fromJson(json, StreetFilterUpdateRequest.class);
		if (request == null)
			return badRequest();

		StreetFilter sf = filterRepository.updateStreetFilter(id, request.name, request.country, request.zip,
				request.description,
				request.platformAccountGroupIds);
		GenericFilterResponse result = genericFilterResponse(sf.getId(), sf.getName(), sf.getDescription(),
				sf.getCountry(), sf.getZip(), sf.getPlatformAccountGroupIds());
		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Email Filter Update Response", response = EmailFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$EmailFilterRequest", paramType = "body") })
	public Result emailFilterUpdate(Long id) {
		final JsonNode json = request().body().asJson();
		if (json == null || id == null)
			return badRequest();

		final EmailFilterRequest request = Json.fromJson(json, EmailFilterRequest.class);
		if (request == null)
			return badRequest();

		EmailFilter emailFilter = filterRepository.updateEmailFilter(id, request.name, request.description,
				request.platformAccountGroupIds);
		return ok(Json.toJson(getEmailFilterResponse(emailFilter)));
	}

	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$PropertyRequest", paramType = "body") })
	public Result propertyUpdate(String name) {
		final JsonNode json = request().body().asJson();
		final PropertyRequest request = Json.fromJson(json, PropertyRequest.class);
		if (request == null)
			return badRequest();

		configRepository.setValue(name, request.value);
		return ok();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Phone Filter Update Response", response = PhoneFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$PhoneFilterRequest", paramType = "body") })
	public Result phoneFilterUpdate(Long id) {
		final JsonNode json = request().body().asJson();
		if (json == null || id == null)
			return badRequest();

		final PhoneFilterRequest request = Json.fromJson(json, PhoneFilterRequest.class);
		if (request == null)
			return badRequest();

		PhoneFilter phoneFilter = filterRepository.updatePhoneFilter(id, request.name, request.description,
				request.platformAccountGroupIds);
		return ok(Json.toJson(getPhoneFilterResponse(phoneFilter)));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Filter deleted"),
		@ApiResponse(code = 400, message = "Bad Request") })
	public Result streetFilterDelete(Long id) {
		if (id == null)
			return badRequest();

		filterRepository.deleteStreetFilter(id);
		return ok();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Filter deleted"),
		@ApiResponse(code = 400, message = "Bad Request") })
	public Result emailFilterDelete(Long id) {
		if (id == null)
			return badRequest();

		filterRepository.deleteEmailFilter(id);
		return ok();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Filter deleted"),
		@ApiResponse(code = 400, message = "Bad Request") })
	public Result phoneFilterDelete(Long id) {
		if (id == null)
			return badRequest();

		filterRepository.deletePhoneFilter(id);
		return ok();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Company Filter Updated Response", response = GenericFilterResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$CompanyFilterUpdateRequest", paramType = "body") })
	public Result companyFilterUpdate(Long id) {
		final JsonNode json = request().body().asJson();
		if (json == null || id == null)
			return badRequest();

		final CompanyFilterUpdateRequest request = Json.fromJson(json, CompanyFilterUpdateRequest.class);
		if (request == null)
			return badRequest();

		CompanyFilter cf = filterRepository.updateCompanyFilter(id, request.name, request.country, request.zip,
				request.description,
				request.platformAccountGroupIds);
		GenericFilterResponse result = genericFilterResponse(cf.getId(), cf.getName(), cf.getDescription(),
				cf.getCountry(), cf.getZip(), cf.getPlatformAccountGroupIds());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Filter deleted"),
		@ApiResponse(code = 400, message = "Bad Request") })
	public Result companyFilterDelete(Long id) {
		if (id == null)
			return badRequest();

		filterRepository.deleteCompanyFilter(id);
		return ok();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Created or Updated"),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$ManualReviewRequest", paramType = "body") })
	public Result manualReviewRequest(long orderId) {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		ManualReviewRequest request = Json.fromJson(json, ManualReviewRequest.class);
		if (request == null)
			return badRequest();

		reviewRepository.saveReviewRequest(orderId, request.reasons.stream()
				.map(x -> reviewRepository.newReviewReason(x.type, x.value)).collect(Collectors.toList()));

		return ok();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Created or Updated"),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$ManualReviewResult", paramType = "body") })
	public Result manualReviewResult(long orderId) {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		ManualReviewResult result = Json.fromJson(json, ManualReviewResult.class);
		if (result == null)
			return badRequest();

		reviewRepository.saveReviewResult(orderId, result.action);
		return ok();
	}

	private ManualReviewGet manualReviewsGet(List<ReviewRequest> reviewRequests, List<ReviewResult> reviewResults) {
		final Map<Long, ManualReviewGetReason> reasons = new TreeMap<>();
		final Map<Long, ManualReviewGetOrder> orders = new TreeMap<>();

		for (ReviewRequest curRequest : reviewRequests) {
			if (orders.get(curRequest.getOrderId()) == null)
				orders.put(curRequest.getOrderId(), new ManualReviewGetOrder());

			orders.get(curRequest.getOrderId()).id = curRequest.getOrderId();
			orders.get(curRequest.getOrderId()).reasons = curRequest.getReviewReasons().stream().map(x -> x.getId())
					.sorted().collect(Collectors.toList());

			for (ReviewReason curReason : curRequest.getReviewReasons()) {
				if (reasons.get(curReason.getId()) == null)
					reasons.put(curReason.getId(), new ManualReviewGetReason());

				reasons.get(curReason.getId()).id = curReason.getId();
				reasons.get(curReason.getId()).type = curReason.getType();
				reasons.get(curReason.getId()).value = curReason.getValue();
			}
		}

		for (ReviewResult cur : reviewResults) {
			if (orders.get(cur.getOrderId()) == null)
				orders.put(cur.getOrderId(), new ManualReviewGetOrder());

			orders.get(cur.getOrderId()).id = cur.getOrderId();
			orders.get(cur.getOrderId()).action = cur.getAction();
		}

		final ManualReviewGet result = new ManualReviewGet();
		result.orders = new ArrayList<>(orders.values());
		result.reasons = new ArrayList<>(reasons.values());

		return result;
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Manual Reviews", response = ManualReviewGet.class) })
	public Result manualReviewsGet() {
		return ok(Json.toJson(
				manualReviewsGet(reviewRepository.findAllReviewRequests(), reviewRepository.findAllReviewResults())));
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Manual Reviews", response = ManualReviewGet.class) })
	public Result manualReviewsGetTimeFilter(Long seconds) {
		return ok(Json.toJson(manualReviewsGet(reviewRepository.findReviewRequestsTimeFilter(seconds),
				reviewRepository.findReviewResultsTimeFilter(seconds))));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Test Runs List Response", response = TestRunsGetResponse.class) })
	public Result testRuns(Integer limit) {

		final TestRunsGetResponse result = new TestRunsGetResponse();
		result.tests = testRunRepository.getTestRuns(limit).stream().map(testRun -> getTestRunResponse(testRun))
				.collect(Collectors.toList());
		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Properties List Response", response = PropertiesGetResponse.class) })
	public Result propertiesGet() {

		final PropertiesGetResponse result = new PropertiesGetResponse();
		result.properties = configRepository.findAllProperties().stream().map(prop -> getPropertyResponse(prop))
				.collect(Collectors.toList());
		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Street Filter List Response", response = GenericFilterGet.class) })
	public Result streetFiltersGet() {
		return ok(Json.toJson(genericFilterGet(filterRepository.findAllStreetFilters().stream()
				.map(x -> genericFilterResponse(x.getId(), x.getName(), x.getDescription(), x.getCountry(), x.getZip(),
						x.getPlatformAccountGroupIds()))
				.collect(Collectors.toList()))));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Street Filter Get Response", response = GenericFilterResponse.class) })
	public Result streetFilterGet(Long id) {
		StreetFilter filter = filterRepository.findStreetFilter(id);
		return filter != null ? ok(Json.toJson(genericFilterResponse(filter.getId(), filter.getName(),
				filter.getDescription(), filter.getCountry(), filter.getZip(), filter.getPlatformAccountGroupIds())))
				: notFound();
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Email Filter List Response", response = EmailFilterGet.class) })
	public Result emailFiltersGet() {

		EmailFilterGet result = new EmailFilterGet();

		result.filters = filterRepository.findAllEmailFilters().stream().map(filter -> getEmailFilterResponse(filter))
				.collect(Collectors.toList());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Email Filter Get Response", response = EmailFilterResponse.class) })
	public Result emailFilterGet(Long id) {
		EmailFilter filter = filterRepository.findEmailFilter(id);
		return filter != null ? ok(Json.toJson(getEmailFilterResponse(filter))) : notFound();
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Phone Filter List Response", response = PhoneFilterGet.class) })
	public Result phoneFiltersGet() {

		PhoneFilterGet result = new PhoneFilterGet();
		result.filters = filterRepository.findAllPhoneFilters().stream().map(filter -> getPhoneFilterResponse(filter))
				.collect(Collectors.toList());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Phone Filter Get Response", response = PhoneFilterResponse.class) })
	public Result phoneFilterGet(Long id) {
		PhoneFilter filter = filterRepository.findPhoneFilter(id);
		return filter != null ? ok(Json.toJson(getPhoneFilterResponse(filter))) : notFound();
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Company Filter List Response", response = GenericFilterGet.class) })
	public Result companyFiltersGet() {
		return ok(Json.toJson(genericFilterGet(filterRepository.findAllCompanyFilters().stream()
				.map(x -> genericFilterResponse(x.getId(), x.getName(), x.getDescription(), x.getCountry(), x.getZip(),
						x.getPlatformAccountGroupIds()))
				.collect(Collectors.toList()))));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Company Filter Get Response", response = GenericFilterResponse.class) })
	public Result companyFilterGet(Long id) {
		CompanyFilter filter = filterRepository.findCompanyFilter(id);
		return filter != null ? ok(Json.toJson(genericFilterResponse(filter.getId(), filter.getName(),
				filter.getDescription(), filter.getCountry(), filter.getZip(), filter.getPlatformAccountGroupIds())))
				: notFound();
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Dashboard Response", response = DashboardGet.class) })
	public Result dashboardGet() {

		final DashboardGet result = new DashboardGet();
		result.personFilterCount = filterRepository.getCountOfPersonFilters();
		result.companyFilterCount = filterRepository.getCountOfCompanyFilters();
		result.streetFilterCount = filterRepository.getCountOfStreetFilters();
		result.emailFilterCount = filterRepository.getCountOfEmailFilters();
		result.phoneFilterCount = filterRepository.getCountOfPhoneFilters();
		List<TestRun> testRuns = testRunRepository.getTestRuns(1);
		if (testRuns.size() == 1) {
			TestRun mostRecent;
			mostRecent = testRuns.get(0);
			result.testRun = new TestRunDashboard();
			result.testRun.id = mostRecent.getId();
			result.testRun.result = mostRecent.getResult();
		}

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Person Filter List Response", response = GenericFilterGet.class) })
	public Result personFiltersGet() {
		return ok(Json.toJson(genericFilterGet(filterRepository.findAllPersonFilters().stream()
				.map(x -> genericFilterResponse(x.getId(), x.getName(), x.getDescription(), x.getCountry(), x.getZip(),
						x.getPlatformAccountGroupIds()))
				.collect(Collectors.toList()))));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Person Filter Get Response", response = GenericFilterResponse.class) })
	public Result personFilterGet(Long id) {
		PersonFilter filter = filterRepository.findPersonFilter(id);
		return filter != null ? ok(Json.toJson(genericFilterResponse(filter.getId(), filter.getName(),
				filter.getDescription(), filter.getCountry(), filter.getZip(), filter.getPlatformAccountGroupIds())))
				: notFound();
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "Countries List Response", response = CountriesGet.class) })
	public Result countriesGet() {

		final CountriesGet result = new CountriesGet();
		result.countries = countryRepository.getCountries().stream().map(country -> getCountryResponse(country))
				.collect(Collectors.toList());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "Platform Account Group List Response", response = PlatformAccountGroupGetResponse.class) })
	public Result platformAccountGroupsGet() {
		PlatformAccountGroupGetResponse result = new PlatformAccountGroupGetResponse();
		result.response = platformAccountGroupRepository.findAllPlatformAccountGroups().stream()
				.map(group -> getPlatformAccountGroupResponse(group)).collect(Collectors.toList());
		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Platform Account Group Get Response", response = PlatformAccountGroupResponse.class) })
	public Result platformAccountGroupGet(Long id) {		
		PlatformAccountGroup group = platformAccountGroupRepository.findPlatformAccountGroup(id);
		return group != null ? ok(Json.toJson(getPlatformAccountGroupResponse(group)))
				: notFound();
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "PlatformAccountGroup Save Response"),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$PlatformAccountGroupRequest", paramType = "body") })
	public Result platformAccountGroupsSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final PlatformAccountGroupRequest request = Json.fromJson(json, PlatformAccountGroupRequest.class);
		if (request == null)
			return badRequest();

		PlatformAccountGroup group = platformAccountGroupRepository.savePlatformAccountGroup(request.name,
				request.description,
				request.accounts);

		return ok(Json.toJson(getPlatformAccountGroupResponse(group)));
	}
	
	@ApiResponses({
			@ApiResponse(code = 200, message = "PlatformAccountGroup Update Response"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$PlatformAccountGroupRequest", paramType = "body") })
	public Result platformAccountGroupsUpdate(Long id) {

		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final PlatformAccountGroupRequest request = Json.fromJson(json, PlatformAccountGroupRequest.class);
		if (request == null)
			return badRequest();

		PlatformAccountGroup group = platformAccountGroupRepository
				.updatePlatformGroup(id, request.name, request.description, request.accounts);

		return ok(Json.toJson(getPlatformAccountGroupResponse(group)));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "PlatformAccountGroup deleted"),
		@ApiResponse(code = 400, message = "Bad Request") })
	public Result platformAccountGroupsDelete(Long id) {

		if (id == null)
			return badRequest();

		platformAccountGroupRepository.deletePlatformAccountGroup(id);
		return ok();
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "Filter History List Response", response = FilterHistoriesGet.class) })
	public Result filterHistoriesGet(Long id) {
		final FilterHistoriesGet result = new FilterHistoriesGet();
		result.filterHistoryResponses = filterRepository.retrieveHistory(id).stream()
				.map(x -> getFilterHistoryResponse(x)).collect(Collectors.toList());

		return ok(Json.toJson(result));
	}

	@ApiResponses({ @ApiResponse(code = 200, message = "ZipCode saved"),
			@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
			@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$ZipCodeRequest", paramType = "body") })
	public Result zipCodesSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final ZipCodeRequest request = Json.fromJson(json, ZipCodeRequest.class);
		if (request == null)
			return badRequest();
		ZipCode zipCode = zipCodeRepository.saveZipCode(request.zip, request.placeName,
				request.countryCode,
				request.latitude, request.longitude);

		ZipCodeResponse result = getZipCodeResponse(zipCode.getZip(), zipCode.getPlaceName(), zipCode.getCountryCode(),
				zipCode.getLatitude(), zipCode.getLongitude());

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Zip Code Response", response = ZipCodeResponse.class) })
	public Result zipCodesGet(String countryCode, String zip) {

		if (StringUtils.isBlank(zip) || StringUtils.isBlank(countryCode))
			return badRequest();

		ZipCode zipCode = zipCodeRepository.getZipCode(countryCode, zip);

		ZipCodesGet result = new ZipCodesGet();
		if (zipCode != null) {
			result.response = getZipCodeResponse(zipCode.getZip(), zipCode.getPlaceName(), zipCode.getCountryCode(),
					zipCode.getLatitude(), zipCode.getLongitude());
		}

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "all Filters Save Response", response = AllFiltersSaveResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$AllFiltersSaveRequest", paramType = "body") })
	@Transactional
	public Result allFiltersSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final AllFiltersSaveRequest request = Json.fromJson(json, AllFiltersSaveRequest.class);
		if (request == null)
			return badRequest();

		CompanyFilter cf = null;
		StreetFilter sf = null;
		PersonFilter pf = null;
		PhoneFilter phf = null;
		EmailFilter ef = null;

		if (StringUtils.isNotBlank(request.companyName)) {
			cf = filterRepository.saveCompanyFilter(request.companyName, request.country, request.zip,
					request.description, request.platformAccountGroupIds);
		}
		if (StringUtils.isNotBlank(request.streetName)) {
			sf = filterRepository.saveStreetFilter(request.streetName, request.country, request.zip,
					request.description, request.platformAccountGroupIds);
		}
		if (StringUtils.isNotBlank(request.personName)) {
			pf = filterRepository.savePersonFilter(request.personName, request.country, request.zip,
					request.description, request.platformAccountGroupIds);
		}
		if (StringUtils.isNotBlank(request.phoneName)) {
			phf = filterRepository.savePhoneFilter(request.phoneName, request.description,
					request.platformAccountGroupIds);
		}
		if (StringUtils.isNotBlank(request.emailName)) {
			ef = filterRepository.saveEmailFilter(request.emailName, request.description,
					request.platformAccountGroupIds);
		}

		AllFiltersSaveResponse result = getAllFiltersSaveResponse(cf, sf, pf, phf, ef);

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Test Data Sets Save Response", response = TestDataSetResponse.class),
		@ApiResponse(code = 400, message = "Bad Request") })
	@ApiImplicitParams({
		@ApiImplicitParam(name = "body", required = true, dataType = "controllers.ApiV1Controller$TestDataSetRequest", paramType = "body") })
	public Result testDataSetSave() {
		final JsonNode json = request().body().asJson();
		if (json == null)
			return badRequest();

		final TestDataSetRequest request = Json.fromJson(json, TestDataSetRequest.class);
		if (request == null)
			return badRequest();

		TestDataSet testDataSet = testDataSetRepository.saveTestDataSet(request.timeoutMilliseconds,
				request.expectAccept, request.expectReview, request.performance);

		TestDataSetResponse result = getTestDataSetResponse(testDataSet);

		return ok(Json.toJson(result));
	}

	@ApiResponses({
		@ApiResponse(code = 200, message = "Test Data Set Get Response", response = TestDataSetResponse.class) })
	public Result testDataSetGet() {

		TestDataSet testDataSet = testDataSetRepository.getTestDataSet();

		return testDataSet != null ? ok(Json.toJson(getTestDataSetResponse(testDataSet))) : notFound();

	}

	private CountryResponse getCountryResponse(Country country) {
		final CountryResponse result = new CountryResponse();
		result.code = country.getCode();
		result.name = country.getName();
		return result;
	}

	private GenericFilterGet genericFilterGet(List<GenericFilterResponse> filters) {
		final GenericFilterGet result = new GenericFilterGet();
		result.filters = filters;
		return result;
	}

	private GenericFilterResponse genericFilterResponse(Long id, String name, String description, String country,
			String zip,
			Long[] platformAccountGroupIds) {
		final GenericFilterResponse result = new GenericFilterResponse();
		result.id = id;
		result.name = name;
		result.description = description;
		result.country = country;
		result.zip = zip;
		result.platformAccountGroupIds = platformAccountGroupIds;
		return result;
	}

	private TestCaseResponse getTestCaseResponse(TestCase testCase) {
		final TestCaseResponse result = new TestCaseResponse();
		result.id = testCase.getId();
		result.name = testCase.getName();
		result.expected = testCase.getExpected();
		result.actual = testCase.getActual();
		result.result = testCase.getResult();
		return result;
	}

	private EmailFilterResponse getEmailFilterResponse(EmailFilter filter) {
		final EmailFilterResponse result = new EmailFilterResponse();
		result.id = filter.getId();
		result.name = filter.getName();
		result.description = filter.getDescription();
		result.platformAccountGroupIds = filter.getPlatformAccountGroupIds();
		return result;
	}

	private PropertyResponse getPropertyResponse(Property prop) {
		final PropertyResponse response = new PropertyResponse();
		response.name = prop.getName();
		response.value = prop.getValue();
		response.description = prop.getDescription();
		return response;
	}

	private PhoneFilterResponse getPhoneFilterResponse(PhoneFilter filter) {
		final PhoneFilterResponse result = new PhoneFilterResponse();
		result.id = filter.getId();
		result.name = filter.getName();
		result.description = filter.getDescription();
		result.platformAccountGroupIds = filter.getPlatformAccountGroupIds();
		return result;
	}

	private TestRunResponse getTestRunResponse(TestRun testRun) {
		final TestRunResponse result = new TestRunResponse();
		result.id = testRun.getId();
		result.result = testRun.getResult();
		result.start = testRun.getStart();
		result.end = testRun.getEnd();
		result.testCases = new ArrayList<TestCaseResponse>();
		for (TestCase curr : testRun.getTestCases()) {
			result.testCases.add(getTestCaseResponse(curr));
		}

		return result;
	}

	static class Ping {
		public final Date date = new Date();
		public final String status = "OK";
	}

	static class TestRunsGetResponse {
		public List<TestRunResponse> tests;
	}

	static class PropertiesGetResponse {
		public List<PropertyResponse> properties;
	}

	static class ManualReviewResult {
		public String action;
	}

	static class ManualReviewRequest {
		@ApiModelProperty(position = 1, required = true)
		public List<ManualReviewReason> reasons;
	}

	static class ManualReviewReason {
		public String type;
		public String value;
	}

	static class ManualReviewGet {
		public List<ManualReviewGetReason> reasons = new ArrayList<>();
		public List<ManualReviewGetOrder> orders = new ArrayList<>();
	}

	static class GenericFilterGet {
		public List<GenericFilterResponse> filters = new ArrayList<>();
	}

	static class CountriesGet {
		public List<CountryResponse> countries = new ArrayList<>();
	}

	static class CountryResponse {
		public String code;
		public String name;
	}

	static class DashboardGet {
		public TestRunDashboard testRun;
		public int streetFilterCount;
		public int companyFilterCount;
		public int personFilterCount;
		public int emailFilterCount;
		public int phoneFilterCount;
	}

	static class TestRunDashboard {
		public Long id;
		public TestResult result;
	}

	static class EmailFilterGet {
		public List<EmailFilterResponse> filters = new ArrayList<>();
	}

	static class PhoneFilterGet {
		public List<PhoneFilterResponse> filters = new ArrayList<>();
	}

	static class ManualReviewGetReason {
		public Long id;
		public String type;
		public String value;
	}

	static class ManualReviewGetOrder {
		public List<Long> reasons = new ArrayList<>();
		public Long id;
		public String action;
	}

	static class DecideAddress {
		public Long id;
		public String firstname;
		public String lastname;
		public String company;
		public String address1;
		public String address2;
		public String address3;
		public String zip;
		public String city;
		public String phone;
		public String country;
	}

	static class DecideUser {
		public Long id;
		public String firstname;
		public String lastname;
		public String username;
		public String mail;
	}

	static class DecideRequest {
		@ApiModelProperty(position = 1, required = true)
		public DecideUser user;
		@ApiModelProperty(position = 2)
		public List<DecideAddress> addresses;
		@ApiModelProperty(position = 3)
		public String platformAccountId;
	}

	static class DecideResult {
		public DecideDecision order;
	}

	static class DecideDecision {
		public DecideOutcome decision;
		public List<DecideReason> reasons = new ArrayList<>();
	}

	static enum DecideOutcome {
		ACCEPT, REVIEW
	}

	static class DecideReason {
		public String type;
		public String value;
		public String message;
	}

	static class GenericFilterRequest {
		@ApiModelProperty(position = 1, required = true, example = "filter name")
		public String name;
		@ApiModelProperty(position = 2, example = "DE", value = "if entered must exist, format ISO 3166-1 alpha-2")
		public String country;
		@ApiModelProperty(position = 3, example = "1010")
		public String zip;
		@ApiModelProperty(position = 4, example = "brief description")
		public String description;
		@ApiModelProperty(position = 5, notes = "Comma separated Platform account group Ids")
		public Long[] platformAccountGroupIds;
	}

	static class EmailFilterRequest {
		@ApiModelProperty(position = 1, required = true, example = "john_smith@gmail.com")
		public String name;
		@ApiModelProperty(position = 2, example = "brief description")
		public String description;
		@ApiModelProperty(position = 3, notes = "comma separated Platform Account Group Ids")
		public Long[] platformAccountGroupIds;
	}

	static class PropertyRequest {
		@ApiModelProperty(position = 1, required = true, example = "86400")
		public String value;
	}

	static class PhoneFilterRequest {
		@ApiModelProperty(position = 1, required = true, example = "+43 660 904 9071")
		public String name;
		@ApiModelProperty(position = 2, example = "brief description")
		public String description;
		@ApiModelProperty(position = 3, notes = "Comma separated Platform Account Group Ids")
		public Long[] platformAccountGroupIds;
	}

	static class ZipCodeRequest {
		@ApiModelProperty(position = 1, required = true, example = "1010")
		public String zip;
		@ApiModelProperty(position = 2, required = true, example = "AT")
		public String countryCode;
		@ApiModelProperty(position = 3, required = false, example = "Wien")
		public String placeName;
		@ApiModelProperty(position = 4, required = true, example = "48.2077")
		public String latitude;
		@ApiModelProperty(position = 5, required = true, example = "16.3705")
		public String longitude;
	}

	static class GenericFilterResponse {
		public Long id;
		public String name;
		public String country;
		public String zip;
		public String description;
		public Long[] platformAccountGroupIds;
	}

	static class EmailFilterResponse {
		public Long id;
		public String name;
		public String description;
		public Long[] platformAccountGroupIds;
	}

	static class PhoneFilterResponse {
		public Long id;
		public String name;
		public String description;
		public Long[] platformAccountGroupIds;
	}

	static class PersonFilterUpdateRequest extends GenericFilterRequest {
	}

	static class CompanyFilterUpdateRequest extends GenericFilterRequest {
	}

	static class StreetFilterUpdateRequest extends GenericFilterRequest {
	}

	static class TestRunRequest {
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT, timezone = DATE_TIMEZONE)
		@ApiModelProperty(position = 1, required = true)
		public Date start;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT, timezone = DATE_TIMEZONE)
		@ApiModelProperty(position = 2, required = true)
		public Date end;
		@ApiModelProperty(position = 3, required = true)
		public List<TestCaseRequest> testCases;
	}

	static class TestCaseRequest {
		@ApiModelProperty(position = 1, required = true, example = "testcase 1")
		public String name;
		@ApiModelProperty(position = 2, required = true, example = "hello")
		public String expected;
		@ApiModelProperty(position = 3, required = true, example = "hello")
		public String actual;
	}

	static class TestRunResponse {
		public Long id;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT, timezone = DATE_TIMEZONE)
		public Date start;
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT, timezone = DATE_TIMEZONE)
		public Date end;
		public List<TestCaseResponse> testCases;
		public TestResult result;
	}

	static class PropertyResponse {
		public String name;
		public String value;
		public String description;
	}

	static class TestCaseResponse {
		public Long id;
		public String name;
		public String expected;
		public String actual;
		public TestResult result;
	}

	static class PlatformAccountGroupRequest {
		@ApiModelProperty(position = 1, required = true, example = "germanPlatforms")
		public String name;
		@ApiModelProperty(position = 2, example = "brief description")
		public String description;
		@ApiModelProperty(position = 3, required = true, notes = "accounts array with comma separated")
		public String[] accounts;
	}

	private PlatformAccountGroupResponse getPlatformAccountGroupResponse(PlatformAccountGroup group) {
		final PlatformAccountGroupResponse response = new PlatformAccountGroupResponse();
		response.id = group.getId();
		response.name = group.getName();
		response.description = group.getDescription();
		response.accounts = group.getAccounts();
		return response;
	}

	static class PlatformAccountGroupGetResponse {
		public List<PlatformAccountGroupResponse> response = new ArrayList<>();
	}

	static class PlatformAccountGroupResponse {
		@ApiModelProperty(position = 1, example = "100", notes = "Unique identifier")
		public Long id;
		@ApiModelProperty(position = 2, required = true, example = "germanPlatforms", notes = "Name of the platform account group")
		public String name;
		@ApiModelProperty(position = 3, example = "brief description", notes = "Description about the platform account group")
		public String description;
		@ApiModelProperty(position = 4, notes = "Accounts associated with the platform account group")
		public String[] accounts;
	}

	static class FilterHistoriesGet {
		public List<FilterHistoryResponse> filterHistoryResponses = new ArrayList<>();
	}

	static class FilterHistoryResponse {
		public Long filterId;
		public String name;
		public String action;
		public String type;
		public String modified;
		public String modifiedBy;
	}

	private FilterHistoryResponse getFilterHistoryResponse(FilterHistory history) {
		FilterHistoryResponse response = new FilterHistoryResponse();
		response.filterId = history.getFilterId();
		response.name = history.getName();
		response.action = history.getAction();
		response.type = history.getType();
		response.modified = history.getModified();
		response.modifiedBy = history.getModifiedBy();
		return response;
	}

	static class ZipCodeResponse {
		@ApiModelProperty(position = 1, example = "1010", notes = "zip code value")
		public String zip;
		@ApiModelProperty(position = 2, example = "AT", notes = "country code value")
		public String countryCode;
		@ApiModelProperty(position = 3, example = "Wien", notes = "place name")
		public String placeName;
		@ApiModelProperty(position = 4, example = "48.2077", notes = "latitude value")
		public String latitude;
		@ApiModelProperty(position = 5, example = "16.3705", notes = "longitude value")
		public String longitude;
	}

	private ZipCodeResponse getZipCodeResponse(String zip, String placeName, String countryCode, String latitude,
			String longitude) {
		final ZipCodeResponse result = new ZipCodeResponse();
		
		result.zip = zip;
		result.countryCode = countryCode;
		result.placeName = placeName;
		result.latitude = latitude;
		result.longitude = longitude;
		
		return result;
	}

	static class ZipCodesGet {
		public ZipCodeResponse response;
	}

	static class AllFiltersSaveRequest {
		@ApiModelProperty(position = 1, required = true, example = "company filter name")
		public String companyName;
		@ApiModelProperty(position = 2, required = true, example = "street filter name")
		public String streetName;
		@ApiModelProperty(position = 3, required = true, example = "+43 660 904 9071")
		public String phoneName;
		@ApiModelProperty(position = 4, required = true, example = "john_smith@gmail.com")
		public String emailName;
		@ApiModelProperty(position = 5, required = true, example = "person filter name")
		public String personName;
		@ApiModelProperty(position = 6, example = "AT", value = "if entered must exist, format ISO 3166-1 alpha-2")
		public String country;
		@ApiModelProperty(position = 6, example = "1010")
		public String zip;
		@ApiModelProperty(position = 8, example = "brief description")
		public String description;
		@ApiModelProperty(position = 9, notes = "Comma separated Platform account group Ids")
		public Long[] platformAccountGroupIds;
	}

	private AllFiltersSaveResponse getAllFiltersSaveResponse(CompanyFilter cf, StreetFilter sf,
			PersonFilter pf, PhoneFilter phf, EmailFilter ef) {
		AllFiltersSaveResponse response = new AllFiltersSaveResponse();

		if (cf != null) {
			response.companyFilterResponse = genericFilterResponse(cf.getId(), cf.getName(), cf.getDescription(),
					cf.getCountry(), cf.getZip(), cf.getPlatformAccountGroupIds());
		}
		if (sf != null) {
			response.streetFilterResponse = genericFilterResponse(sf.getId(), sf.getName(), sf.getDescription(),
					sf.getCountry(), sf.getZip(), sf.getPlatformAccountGroupIds());
		}
		if (pf != null) {
			response.personFilterResponse = genericFilterResponse(pf.getId(), pf.getName(), pf.getDescription(),
					pf.getCountry(), pf.getZip(), pf.getPlatformAccountGroupIds());
		}
		if (phf != null) {
			response.phoneFilterResponse = getPhoneFilterResponse(phf);
		}
		if (ef != null) {
			response.emailFilterResponse = getEmailFilterResponse(ef);
		}

		return response;
	}

	static class AllFiltersSaveResponse {
		public GenericFilterResponse companyFilterResponse;
		public GenericFilterResponse streetFilterResponse;
		public GenericFilterResponse personFilterResponse;
		public PhoneFilterResponse phoneFilterResponse;
		public EmailFilterResponse emailFilterResponse;
	}

	static class TestDataSetRequest {
		@ApiModelProperty(position = 1, required = true)
		public long timeoutMilliseconds;
		@ApiModelProperty(position = 2, required = true)
		public long[] expectAccept;
		@ApiModelProperty(position = 3, required = true)
		public long[] expectReview;
		@ApiModelProperty(position = 4, required = true)
		public long[] performance;
	}

	static class TestDataSetResponse {
		public long timeoutMilliseconds;
		public long[] expectAccept;
		public long[] expectReview;
		public long[] performance;
	}

	private TestDataSetResponse getTestDataSetResponse(TestDataSet testDataSet) {
		TestDataSetResponse result = new TestDataSetResponse();
		result.expectAccept = testDataSet.getExpectAccept();
		result.expectReview = testDataSet.getExpectReview();
		result.performance = testDataSet.getPerformance();
		result.timeoutMilliseconds = testDataSet.getTimeoutMilliseconds();
		return result;
	}
}
