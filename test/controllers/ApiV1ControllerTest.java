package controllers;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import controllers.ApiV1Controller.AllFiltersSaveRequest;
import controllers.ApiV1Controller.AllFiltersSaveResponse;
import controllers.ApiV1Controller.CompanyFilterUpdateRequest;
import controllers.ApiV1Controller.CountriesGet;
import controllers.ApiV1Controller.CountryResponse;
import controllers.ApiV1Controller.DashboardGet;
import controllers.ApiV1Controller.DecideAddress;
import controllers.ApiV1Controller.DecideOutcome;
import controllers.ApiV1Controller.DecideRequest;
import controllers.ApiV1Controller.DecideResult;
import controllers.ApiV1Controller.DecideUser;
import controllers.ApiV1Controller.EmailFilterGet;
import controllers.ApiV1Controller.EmailFilterRequest;
import controllers.ApiV1Controller.EmailFilterResponse;
import controllers.ApiV1Controller.FilterHistoriesGet;
import controllers.ApiV1Controller.FilterHistoryResponse;
import controllers.ApiV1Controller.GenericFilterGet;
import controllers.ApiV1Controller.GenericFilterRequest;
import controllers.ApiV1Controller.GenericFilterResponse;
import controllers.ApiV1Controller.ManualReviewGet;
import controllers.ApiV1Controller.ManualReviewGetOrder;
import controllers.ApiV1Controller.ManualReviewGetReason;
import controllers.ApiV1Controller.ManualReviewReason;
import controllers.ApiV1Controller.ManualReviewRequest;
import controllers.ApiV1Controller.ManualReviewResult;
import controllers.ApiV1Controller.PersonFilterUpdateRequest;
import controllers.ApiV1Controller.PhoneFilterGet;
import controllers.ApiV1Controller.PhoneFilterRequest;
import controllers.ApiV1Controller.PhoneFilterResponse;
import controllers.ApiV1Controller.Ping;
import controllers.ApiV1Controller.PlatformAccountGroupGetResponse;
import controllers.ApiV1Controller.PlatformAccountGroupRequest;
import controllers.ApiV1Controller.PlatformAccountGroupResponse;
import controllers.ApiV1Controller.PropertiesGetResponse;
import controllers.ApiV1Controller.PropertyRequest;
import controllers.ApiV1Controller.StreetFilterUpdateRequest;
import controllers.ApiV1Controller.TestCaseRequest;
import controllers.ApiV1Controller.TestCaseResponse;
import controllers.ApiV1Controller.TestDataSetRequest;
import controllers.ApiV1Controller.TestDataSetResponse;
import controllers.ApiV1Controller.TestRunDashboard;
import controllers.ApiV1Controller.TestRunRequest;
import controllers.ApiV1Controller.TestRunResponse;
import controllers.ApiV1Controller.TestRunsGetResponse;
import controllers.ApiV1Controller.ZipCodeRequest;
import controllers.ApiV1Controller.ZipCodeResponse;
import controllers.ApiV1Controller.ZipCodesGet;
import model.config.Config;
import model.config.ConfigRepository;
import model.config.ConfigRepositoryFactory;
import model.filter.CompanyFilter;
import model.filter.EmailFilter;
import model.filter.FilterRepository;
import model.filter.FilterRepositoryFactory;
import model.filter.PersonFilter;
import model.filter.PhoneFilter;
import model.filter.StreetFilter;
import model.filter.TestLoginUserCeator;
import model.platformaccountgroup.PlatformAccountGroup;
import model.platformaccountgroup.PlatformAccountGroupRepository;
import model.platformaccountgroup.PlatformAccountGroupRepositoryFactory;
import model.platformaccountgroup.TestPlatformAccountGroupCreator;
import model.review.ReviewReason;
import model.review.ReviewRepository;
import model.review.ReviewRepositoryFactory;
import model.review.ReviewRequest;
import model.review.ReviewResult;
import model.testdataset.TestDataSetRepository;
import model.testdataset.TestDataSetRepositoryFactory;
import model.testrun.TestCase;
import model.testrun.TestResult;
import model.testrun.TestRun;
import model.testrun.TestRunRepository;
import model.testrun.TestRunRepositoryFactory;
import model.time.TimeProviderFactory;
import model.zipcode.TestZipCodeCreator;
import model.zipcode.ZipCode;
import model.zipcode.ZipCodeRepository;
import model.zipcode.ZipCodeRepositoryFactory;
import play.libs.Json;
import play.mvc.Result;
import play.test.Helpers;
import util.Utils;

public class ApiV1ControllerTest {

	private static final Long[] platformAccountGroupIds = { 100L };
	private static final int HTTP_OK = 200;
	private static final int HTTP_NOT_FOUND = 404;
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String CHARSET_UTF8 = "UTF-8";
	private static final int NUMBER_OF_COUNTRIES = 248;

	private ReviewRepository reviewRepository;
	private FilterRepository filterRepository;
	private TestRunRepository testRunRepository;
	private ConfigRepository configRepository;
	private ZipCodeRepository zipCodeRepository;
	private ApiV1Controller controller;
	private PlatformAccountGroupRepository platformAccountGroupRepository;
	private TestDataSetRepository testDataSetRepository;

	// test data for test data set
	private static final long TIME_OUT_IN_MS = 5000;
	private static final long[] EXPECT_ACCEPT_DATA = {};
	private static final long[] EXPECT_REVIEW_DATA = {
		2846779,
		3006528,
		3221238,
		3301777,
		3375103,
		5303407,
		5303836,
		5581034,
		5581129,
		5581171,
		5581350,
		5934582,
		6556027,
		6556042,
		6599322,
		7868600,
		8532752,
		9141049,
		9141136,
		10365625,
		11102769,
		11774959,
		13117858,
		14880982,
		14880990,
		14881306,
		15863955,
		16927130,
		16927161,
		17334848,
		17348518,
		17348553,
		17348757,
		17348922,
		18920369,
		18996047 };
	private static final long[] PERFORMANCE_DATA = {
		18818184,
		18823820,
		17025228,
		18814659,
		18828273,
		13185021,
		18407921,
		15682352 };

	private static void resetModelFactories() {
		ReviewRepositoryFactory.FACTORY = ReviewRepositoryFactory
				.factoryCaching(ReviewRepositoryFactory.factoryMemory());
		FilterRepositoryFactory.FACTORY = FilterRepositoryFactory
				.factoryCaching(FilterRepositoryFactory.factoryMemory());
		TestRunRepositoryFactory.FACTORY = TestRunRepositoryFactory
				.factoryCaching(TestRunRepositoryFactory.factoryMemory());
		ConfigRepositoryFactory.FACTORY = ConfigRepositoryFactory
				.factoryCaching(ConfigRepositoryFactory.factoryMemory());
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(0L);
		PlatformAccountGroupRepositoryFactory.FACTORY = PlatformAccountGroupRepositoryFactory
				.factoryCaching(PlatformAccountGroupRepositoryFactory.factoryMemory());
		ZipCodeRepositoryFactory.FACTORY = ZipCodeRepositoryFactory
				.factoryCaching(ZipCodeRepositoryFactory.factoryMemory());
		TestDataSetRepositoryFactory.FACTORY = TestDataSetRepositoryFactory
				.factoryCaching(TestDataSetRepositoryFactory.factoryMemory());
	}

	@Before
	public void before() {
		resetModelFactories();
		this.controller = new ApiV1Controller();
		this.reviewRepository = ReviewRepositoryFactory.get();
		this.filterRepository = FilterRepositoryFactory.get();
		this.testRunRepository = TestRunRepositoryFactory.get();
		this.configRepository = ConfigRepositoryFactory.get();
		this.platformAccountGroupRepository = PlatformAccountGroupRepositoryFactory.get();
		this.zipCodeRepository = ZipCodeRepositoryFactory.get();
		this.testDataSetRepository = TestDataSetRepositoryFactory.get();
		createTestZipCodes();
		TestLoginUserCeator.createHttpBasicAuth();
	}

	@After
	public void after() {
		this.controller = null;
		resetModelFactories();
	}

	@Test
	public void personFilterSave() {
		assertEquals(0, filterRepository.findAllFilters().size());
		final GenericFilterRequest request = new GenericFilterRequest();
		request.name = "fischer";
		request.country = "AT";
		request.zip = "1010";
		request.description = "desc123";
		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.personFilterSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, filterRepository.findAllFilters().size());
		assertEquals("fischer", filterRepository.findAllPersonFilters().get(0).getName());
		assertEquals("AT", filterRepository.findAllPersonFilters().get(0).getCountry());
		assertEquals("1010", filterRepository.findAllPersonFilters().get(0).getZip());
		assertEquals("desc123", filterRepository.findAllPersonFilters().get(0).getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllPersonFilters().get(0).getId(), filter.id);
		assertEquals(request.name, filter.name);
		assertEquals(request.country, filter.country);
		assertEquals(request.zip, filter.zip);
		assertEquals(request.description, filter.description);
	}

	@Test
	public void dashBoardGet() {

		// add some data
		filterRepository.savePersonFilter("John Erikson", "IR", "", "", platformAccountGroupIds);
		filterRepository.savePersonFilter("Hector Gonzalez", "ES", "", "", platformAccountGroupIds);
		filterRepository.savePersonFilter("Jim Morrison", "AU", "", "", platformAccountGroupIds);

		filterRepository.saveCompanyFilter("Facebook", "IR", "", "", platformAccountGroupIds);
		filterRepository.saveCompanyFilter("Google", "ES", "", "", platformAccountGroupIds);

		filterRepository.saveStreetFilter("Madison Avenue", "at", "1010", "", platformAccountGroupIds);

		filterRepository.savePhoneFilter("+43 111 222 3333", "", platformAccountGroupIds);
		filterRepository.savePhoneFilter("+43 111 222 4444", "", platformAccountGroupIds);
		filterRepository.savePhoneFilter("+43 111 222 5555", "", platformAccountGroupIds);
		filterRepository.savePhoneFilter("+43 111 222 6666", "", platformAccountGroupIds);

		filterRepository.saveEmailFilter("first@email.com", null, platformAccountGroupIds);
		filterRepository.saveEmailFilter("second@email.com", null, platformAccountGroupIds);
		filterRepository.saveEmailFilter("third@email.com", null, platformAccountGroupIds);
		filterRepository.saveEmailFilter("fourth@email.com", null, platformAccountGroupIds);
		filterRepository.saveEmailFilter("fifth@email.com", null, platformAccountGroupIds);

		TestCase tc1 = testRunRepository.newTestCase();
		tc1.setName("testcase 1");
		tc1.setActual("hello");
		tc1.setExpected("hello");

		TestCase tc2 = testRunRepository.newTestCase();
		tc2.setName("testcase 2");
		tc2.setActual("5");
		tc2.setExpected("4");

		List<TestCase> testCases = new ArrayList<TestCase>();
		testCases.add(tc1);
		testCases.add(tc2);

		TestRun toSave = testRunRepository.newTestRun();
		toSave.setStart(new Date());
		toSave.setEnd(new Date());
		toSave.setTestCases(testCases);
		toSave = testRunRepository.saveTestRun(toSave);

		// call dashboard
		final Result resultFromApiCall = controller.dashboardGet();
		assertEquals(HTTP_OK, resultFromApiCall.status());
		assertEquals(CONTENT_TYPE_JSON, resultFromApiCall.contentType().get());
		assertEquals(CHARSET_UTF8, resultFromApiCall.charset().get());

		final DashboardGet resultNode = Json.fromJson(Json.parse(Helpers.contentAsString(resultFromApiCall)),
				DashboardGet.class);

		assertEquals(1, resultNode.streetFilterCount);
		assertEquals(2, resultNode.companyFilterCount);
		assertEquals(3, resultNode.personFilterCount);
		assertEquals(4, resultNode.phoneFilterCount);
		assertEquals(5, resultNode.emailFilterCount);

		TestRunDashboard tr1 = new TestRunDashboard();
		tr1.id = toSave.getId();
		tr1.result = toSave.getResult();

		assertEquals(resultNode.testRun.id, tr1.id);
		assertEquals(resultNode.testRun.result, TestResult.FAILED);

	}

	@Test
	public void personFilterUpdate() {
		assertEquals(0, filterRepository.findAllFilters().size());

		filterRepository.savePersonFilter("person", "RU", "", "desc", platformAccountGroupIds);
		PersonFilter p1 = filterRepository.findAllPersonFilters().get(0);

		final PersonFilterUpdateRequest update = new PersonFilterUpdateRequest();
		update.name = "name updated";
		update.country = "AT";
		update.zip = "1010";
		update.description = "description updated";
		update.platformAccountGroupIds = platformAccountGroupIds;

		final Result updated = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.personFilterUpdate(p1.getId()));
		assertEquals(HTTP_OK, updated.status());

		PersonFilter updatedPerson = filterRepository.findAllPersonFilters().get(0);
		assertEquals(p1.getId(), updatedPerson.getId());
		assertEquals("name updated", updatedPerson.getName());
		assertEquals("AT", updatedPerson.getCountry());
		assertEquals("1010", updatedPerson.getZip());
		assertEquals("description updated", updatedPerson.getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(updated)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllPersonFilters().get(0).getId(), filter.id);
		assertEquals(update.name, filter.name);
		assertEquals(update.country, filter.country);
		assertEquals(update.description, filter.description);

	}

	@Test
	public void personFilterDelete() {
		assertEquals(0, filterRepository.findAllFilters().size());

		filterRepository.savePersonFilter("person", "RU", "", "desc", platformAccountGroupIds);
		PersonFilter p1 = filterRepository.findAllPersonFilters().get(0);

		final Result deleted = Helpers.invokeWithContext(Helpers.fakeRequest(),
				() -> controller.personFilterDelete(p1.getId()));
		assertEquals(HTTP_OK, deleted.status());

		assertTrue(filterRepository.findAllPersonFilters().size() == 0);
		assertTrue(filterRepository.findAllFilters().size() == 0);

	}

	@Test
	public void companyFilterSave() {
		assertEquals(0, filterRepository.findAllCompanyFilters().size());
		final GenericFilterRequest request = new GenericFilterRequest();
		request.name = "accenture";
		request.country = "AT";
		request.zip = "1020";
		request.description = "description for the filter";
		request.platformAccountGroupIds = platformAccountGroupIds;

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.companyFilterSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, filterRepository.findAllCompanyFilters().size());
		assertEquals("accenture", filterRepository.findAllCompanyFilters().get(0).getName());
		assertEquals("AT", filterRepository.findAllCompanyFilters().get(0).getCountry());
		assertEquals("1020", filterRepository.findAllCompanyFilters().get(0).getZip());
		assertEquals("description for the filter", filterRepository.findAllCompanyFilters().get(0).getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllCompanyFilters().get(0).getId(), filter.id);
		assertEquals(request.name, filter.name);
		assertEquals(request.country, filter.country);
		assertEquals(request.zip, filter.zip);
		assertEquals(request.description, filter.description);
	}

	@Test
	public void companyFilterWithNumberSave() {
		assertEquals(0, filterRepository.findAllCompanyFilters().size());
		final GenericFilterRequest request = new GenericFilterRequest();
		request.name = "good 2 go";
		request.country = "AT";
		request.zip = "1010";
		request.description = "description for the filter";
		request.platformAccountGroupIds = platformAccountGroupIds;

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.companyFilterSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, filterRepository.findAllCompanyFilters().size());
		assertEquals("good 2 go", filterRepository.findAllCompanyFilters().get(0).getName());
		assertEquals("AT", filterRepository.findAllCompanyFilters().get(0).getCountry());
		assertEquals("1010", filterRepository.findAllCompanyFilters().get(0).getZip());
		assertEquals("description for the filter", filterRepository.findAllCompanyFilters().get(0).getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllCompanyFilters().get(0).getId(), filter.id);
		assertEquals(request.name, filter.name);
		assertEquals(request.country, filter.country);
		assertEquals(request.description, filter.description);
	}

	@Test
	public void companyFilterUpdate() {
		assertEquals(0, filterRepository.findAllFilters().size());
		filterRepository.saveCompanyFilter("google", "CH", "", "desc", platformAccountGroupIds);

		CompanyFilter c1 = filterRepository.findAllCompanyFilters().get(0);

		final CompanyFilterUpdateRequest update = new CompanyFilterUpdateRequest();

		update.name = "name updated";
		update.country = "AT";
		update.zip = "1010";
		update.description = "description updated";
		update.platformAccountGroupIds = platformAccountGroupIds;

		final Result updated = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.companyFilterUpdate(c1.getId()));
		assertEquals(HTTP_OK, updated.status());

		CompanyFilter updatedCompany = filterRepository.findAllCompanyFilters().get(0);
		assertEquals(c1.getId(), updatedCompany.getId());
		assertEquals("name updated", updatedCompany.getName());
		assertEquals("AT", updatedCompany.getCountry());
		assertEquals("1010", updatedCompany.getZip());
		assertEquals("description updated", updatedCompany.getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(updated)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllCompanyFilters().get(0).getId(), filter.id);
		assertEquals(update.name, filter.name);
		assertEquals(update.country, filter.country);
		assertEquals(update.zip, filter.zip);
		assertEquals(update.description, filter.description);

	}

	@Test
	public void companyFilterWithNumberUpdate() {
		assertEquals(0, filterRepository.findAllFilters().size());
		filterRepository.saveCompanyFilter("google", "AT", "1030", "desc", platformAccountGroupIds);

		CompanyFilter c1 = filterRepository.findAllCompanyFilters().get(0);

		final CompanyFilterUpdateRequest update = new CompanyFilterUpdateRequest();

		update.name = "name updated with 2";
		update.country = "JP";
		update.zip = null;
		update.description = "description updated";
		update.platformAccountGroupIds = platformAccountGroupIds;

		final Result updated = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.companyFilterUpdate(c1.getId()));
		assertEquals(HTTP_OK, updated.status());

		CompanyFilter updatedCompany = filterRepository.findAllCompanyFilters().get(0);
		assertEquals(c1.getId(), updatedCompany.getId());
		assertEquals("name updated with 2", updatedCompany.getName());
		assertEquals("JP", updatedCompany.getCountry());
		assertNull(updatedCompany.getZip());
		assertEquals("description updated", updatedCompany.getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(updated)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllCompanyFilters().get(0).getId(), filter.id);
		assertEquals(update.name, filter.name);
		assertEquals(update.country, filter.country);
		assertEquals(update.zip, filter.zip);
		assertEquals(update.description, filter.description);

	}

	@Test
	public void companyFilterDelete() {
		assertEquals(0, filterRepository.findAllFilters().size());
		filterRepository.saveCompanyFilter("google", "CH", "", "desc", platformAccountGroupIds);

		CompanyFilter c1 = filterRepository.findAllCompanyFilters().get(0);

		final Result deleted = Helpers.invokeWithContext(Helpers.fakeRequest(),
				() -> controller.companyFilterDelete(c1.getId()));
		assertEquals(HTTP_OK, deleted.status());

		assertTrue(filterRepository.findAllCompanyFilters().size() == 0);
		assertTrue(filterRepository.findAllFilters().size() == 0);

	}

	@Test
	public void streetFilterSave() {
		assertEquals(0, filterRepository.findAllStreetFilters().size());
		final GenericFilterRequest request = new GenericFilterRequest();
		request.name = "boulevard spain";
		request.country = "AT";
		request.zip = "1010";
		request.description = "description for the street filter";
		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.streetFilterSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, filterRepository.findAllStreetFilters().size());
		assertEquals("boulevard spain", filterRepository.findAllStreetFilters().get(0).getName());
		assertEquals("AT", filterRepository.findAllStreetFilters().get(0).getCountry());
		assertEquals("1010", filterRepository.findAllStreetFilters().get(0).getZip());
		assertEquals("description for the street filter",
				filterRepository.findAllStreetFilters().get(0).getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllStreetFilters().get(0).getId(), filter.id);
		assertEquals(request.name, filter.name);
		assertEquals(request.country, filter.country);
		assertEquals(request.zip, filter.zip);
		assertEquals(request.description, filter.description);
	}

	@Test
	public void streetFilterUpdate() {
		assertEquals(0, filterRepository.findAllFilters().size());
		filterRepository.saveStreetFilter("Boulevard Spain", "ES", "", "desc", platformAccountGroupIds);

		StreetFilter s1 = filterRepository.findAllStreetFilters().get(0);

		final StreetFilterUpdateRequest update = new StreetFilterUpdateRequest();
		update.name = "name updated";
		update.country = "AT";
		update.zip = "1020";
		update.description = "description updated";
		update.platformAccountGroupIds = platformAccountGroupIds;

		final Result updated = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.streetFilterUpdate(s1.getId()));
		assertEquals(HTTP_OK, updated.status());

		StreetFilter updatedStreet = filterRepository.findAllStreetFilters().get(0);
		assertEquals(s1.getId(), updatedStreet.getId());
		assertEquals("name updated", updatedStreet.getName());
		assertEquals("AT", updatedStreet.getCountry());
		assertEquals("1020", updatedStreet.getZip());
		assertEquals("description updated", updatedStreet.getDescription());

		// test response
		final GenericFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(updated)),
				GenericFilterResponse.class);
		assertEquals(filterRepository.findAllStreetFilters().get(0).getId(), filter.id);
		assertEquals(update.name, filter.name);
		assertEquals(update.country, filter.country);
		assertEquals(update.zip, filter.zip);
		assertEquals(update.description, filter.description);

	}

	@Test
	public void streetFilterDelete() {
		assertEquals(0, filterRepository.findAllFilters().size());
		filterRepository.saveStreetFilter("Boulevard Spain", "at", "1010", "desc", platformAccountGroupIds);

		StreetFilter s1 = filterRepository.findAllStreetFilters().get(0);

		final Result deleted = Helpers.invokeWithContext(Helpers.fakeRequest(),
				() -> controller.streetFilterDelete(s1.getId()));
		assertEquals(HTTP_OK, deleted.status());

		assertTrue(filterRepository.findAllStreetFilters().size() == 0);
		assertTrue(filterRepository.findAllFilters().size() == 0);

	}

	@Test
	public void emailFilterSave() {
		assertEquals(0, filterRepository.findAllFilters().size());
		final EmailFilterRequest request = new EmailFilterRequest();
		request.name = "john@gmail.com";
		request.description = "desc123";
		request.platformAccountGroupIds = platformAccountGroupIds;

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.emailFilterSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, filterRepository.findAllFilters().size());
		assertEquals("john@gmail.com", filterRepository.findAllEmailFilters().get(0).getName());
		assertEquals("desc123", filterRepository.findAllEmailFilters().get(0).getDescription());

		// test response
		final EmailFilterResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				EmailFilterResponse.class);
		assertEquals(filterRepository.findAllEmailFilters().get(0).getId(), filter.id);
		assertEquals(request.name, filter.name);
		assertEquals(request.description, filter.description);
	}

	@Test
	public void emailFilterUpdate() {
		assertEquals(0, filterRepository.findAllFilters().size());

		filterRepository.saveEmailFilter("john@yahoo.com", "desc", platformAccountGroupIds);
		EmailFilter e1 = filterRepository.findAllEmailFilters().get(0);

		final EmailFilterRequest update = new EmailFilterRequest();
		update.name = "updated_john@gmail.com";
		update.description = "description updated";
		update.platformAccountGroupIds = platformAccountGroupIds;

		final Result updated = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.emailFilterUpdate(e1.getId()));
		assertEquals(HTTP_OK, updated.status());

		EmailFilter updatedFilter = filterRepository.findAllEmailFilters().get(0);
		assertEquals(e1.getId(), updatedFilter.getId());
		assertEquals("updated_john@gmail.com", updatedFilter.getName());
		assertEquals("description updated", updatedFilter.getDescription());

		// test response
		final EmailFilterResponse filter = Json
				.fromJson(Json.parse(Helpers.contentAsString(updated)), EmailFilterResponse.class);
		assertEquals(filterRepository.findAllEmailFilters().get(0).getId(), filter.id);
		assertEquals(updatedFilter.getName(), filter.name);
		assertEquals(updatedFilter.getDescription(), filter.description);

	}

	@Test
	public void emailFilterDelete() {
		assertEquals(0, filterRepository.findAllFilters().size());

		filterRepository.saveEmailFilter("john@gmail.com", "desc", platformAccountGroupIds);
		EmailFilter e1 = filterRepository.findAllEmailFilters().get(0);

		final Result deleted = Helpers.invokeWithContext(Helpers.fakeRequest(),
				() -> controller.emailFilterDelete(e1.getId()));
		assertEquals(HTTP_OK, deleted.status());

		assertTrue(filterRepository.findAllEmailFilters().size() == 0);
		assertTrue(filterRepository.findAllFilters().size() == 0);

	}

	@Test
	public void phoneFilterSave() {
		assertEquals(0, filterRepository.findAllFilters().size());
		final PhoneFilterRequest request = new PhoneFilterRequest();
		request.name = "6609049071";
		request.description = "desc123";
		request.platformAccountGroupIds = platformAccountGroupIds;

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.phoneFilterSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, filterRepository.findAllFilters().size());
		assertEquals("6609049071", filterRepository.findAllPhoneFilters().get(0).getName());
		assertEquals("desc123", filterRepository.findAllPhoneFilters().get(0).getDescription());
	}

	@Test
	public void phoneFilterUpdate() {
		assertEquals(0, filterRepository.findAllFilters().size());

		filterRepository.savePhoneFilter("6609049071", "desc", platformAccountGroupIds);
		PhoneFilter p1 = filterRepository.findAllPhoneFilters().get(0);

		final PhoneFilterRequest update = new PhoneFilterRequest();
		update.name = "555 666 1111";
		update.description = "description updated";
		update.platformAccountGroupIds = platformAccountGroupIds;

		final Result updated = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.phoneFilterUpdate(p1.getId()));
		assertEquals(HTTP_OK, updated.status());

		PhoneFilter updatedFilter = filterRepository.findAllPhoneFilters().get(0);
		assertEquals(p1.getId(), updatedFilter.getId());
		assertEquals("5556661111", updatedFilter.getName());
		assertEquals("description updated", updatedFilter.getDescription());

	}

	@Test
	public void phoneFilterDelete() {
		assertEquals(0, filterRepository.findAllFilters().size());

		filterRepository.savePhoneFilter("222 333 4444", "desc", platformAccountGroupIds);
		PhoneFilter p1 = filterRepository.findAllPhoneFilters().get(0);

		final Result deleted = Helpers.invokeWithContext(Helpers.fakeRequest(),
				() -> controller.phoneFilterDelete(p1.getId()));
		assertEquals(HTTP_OK, deleted.status());

		assertTrue(filterRepository.findAllPhoneFilters().size() == 0);
		assertTrue(filterRepository.findAllFilters().size() == 0);

	}

	@Test
	public void testRunSavePassed() {
		assertEquals(0, testRunRepository.getTestRuns(20).size());

		final TestCaseRequest tc1 = new TestCaseRequest();
		tc1.name = "testCase1";
		tc1.expected = "true";
		tc1.actual = "true";

		final TestCaseRequest tc2 = new TestCaseRequest();
		tc2.name = "testCase2";
		tc2.expected = "hello";
		tc2.actual = "hello";

		final TestRunRequest request = new TestRunRequest();
		request.start = new Date();
		request.end = new Date();
		request.testCases = new ArrayList<TestCaseRequest>();
		request.testCases.add(tc1);
		request.testCases.add(tc2);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.testRunSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, testRunRepository.getTestRuns(20).size());
		assertEquals(TestResult.PASSED, testRunRepository.getTestRuns(20).get(0).getResult());
	}

	@Test
	public void testRunSaveFailed() {
		assertEquals(0, testRunRepository.getTestRuns(20).size());

		final TestCaseRequest tc1 = new TestCaseRequest();
		tc1.name = "testCase1";
		tc1.expected = "true";
		tc1.actual = "true";

		final TestCaseRequest tc2 = new TestCaseRequest();
		tc2.name = "testCase2";
		tc2.expected = "0";
		tc2.actual = "1";

		final TestRunRequest request = new TestRunRequest();
		request.start = new Date();
		request.end = new Date();
		request.testCases = new ArrayList<TestCaseRequest>();
		request.testCases.add(tc1);
		request.testCases.add(tc2);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.testRunSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, testRunRepository.getTestRuns(20).size());
		assertEquals(TestResult.FAILED, testRunRepository.getTestRuns(20).get(0).getResult());
	}

	@Test
	public void decidePersonMatch() {
		String[] platformAccountId = { "amazon.uk@dodax.com" };
		PlatformAccountGroup platformAccountGroup = platformAccountGroupRepository
				.savePlatformAccountGroup("ukPlatforms", "desc", platformAccountId);
		PersonFilter filter = filterRepository.savePersonFilter("sabine", null, null, null,
				new Long[] { platformAccountGroup.getId() });

		final DecideRequest request = new DecideRequest();
		request.user = decideUserFischer();
		request.addresses = Arrays.asList(decideAddressFischer());
		request.platformAccountId = platformAccountId[0];

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(2, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
		assertTrue(decideResult.order.reasons.get(1).message.contains(filter.getName()));
	}

	@Test
	public void decidePersonNoMatch() {
		String[] platformAccountId = { "amazon.uk@dodax.com" };
		PlatformAccountGroup platformAccountGroup = platformAccountGroupRepository
				.savePlatformAccountGroup("ukPlatforms", "desc", platformAccountId);
		filterRepository.savePersonFilter("NoMatch", null, null, null, new Long[] { platformAccountGroup.getId() });

		final DecideRequest request = new DecideRequest();
		request.user = decideUserFischer();
		request.addresses = Arrays.asList(decideAddressFischer());
		request.platformAccountId = "amazon.us@dodax.com";

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.ACCEPT, decideResult.order.decision);
		assertEquals(0, decideResult.order.reasons.size());
	}

	@Test
	public void decideCompanyMatch() {
		String[] platformAccountId = { "amazon.uk@dodax.com" };
		PlatformAccountGroup platformAccountGroup = platformAccountGroupRepository
				.savePlatformAccountGroup("ukPlatforms", "desc", platformAccountId);
		CompanyFilter filter = filterRepository.saveCompanyFilter("accenture", null, null, null,
				new Long[] { platformAccountGroup.getId() });

		final DecideRequest request = new DecideRequest();
		request.user = decideUserFischer();
		request.addresses = Arrays.asList(decideAddressAccenture());
		request.platformAccountId = platformAccountId[0];

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
	}

	@Test
	public void decideCompanyNoMatch() {
		String[] platformAccountId = { "amazon.uk@dodax.com" };
		PlatformAccountGroup platformAccountGroup = platformAccountGroupRepository
				.savePlatformAccountGroup("ukPlatforms", "desc", platformAccountId);
		filterRepository.saveCompanyFilter("NoMatch", null, null, null, new Long[] { platformAccountGroup.getId() });

		final DecideRequest request = new DecideRequest();
		request.user = decideUserFischer();
		request.addresses = Arrays.asList(decideAddressAccenture());
		request.platformAccountId = "amazon.us@dodax.com";

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.ACCEPT, decideResult.order.decision);
		assertEquals(0, decideResult.order.reasons.size());
	}

	@Test
	public void decideStreetMatch() {
		String[] platformAccountId = { "amazon.uk@dodax.com" };
		PlatformAccountGroup platformAccountGroup = platformAccountGroupRepository
				.savePlatformAccountGroup("ukPlatforms", "desc", platformAccountId);
		StreetFilter filter = filterRepository.saveStreetFilter("boulevard spain", null, null, null,
				new Long[] { platformAccountGroup.getId() });

		final DecideRequest request = new DecideRequest();
		request.user = decideUserFischer();
		request.addresses = Arrays.asList(decideAddressBoulevardSpain());
		request.platformAccountId = platformAccountId[0];

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(2, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
		assertTrue(decideResult.order.reasons.get(1).message.contains(filter.getName()));
	}

	@Test
	public void decideStreetNoMatch() {
		String[] platformAccountId = { "amazon.uk@dodax.com" };
		PlatformAccountGroup platformAccountGroup = platformAccountGroupRepository
				.savePlatformAccountGroup("ukPlatforms", "desc", platformAccountId);
		filterRepository.saveStreetFilter("NoMatch", null, null, null, new Long[] { platformAccountGroup.getId() });

		final DecideRequest request = new DecideRequest();
		request.user = decideUserFischer();
		request.addresses = Arrays.asList(decideAddressBoulevardSpain());
		request.platformAccountId = "amazon.us@dodax.com";

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.ACCEPT, decideResult.order.decision);
		assertEquals(0, decideResult.order.reasons.size());
	}

	private DecideAddress decideAddressFischer() {
		final DecideAddress address = new DecideAddress();
		address.id = 100L;
		address.firstname = "Sabine";
		address.lastname = "Fischer";
		address.company = "SaFi GmbH";
		address.address1 = "Leerstrasse 1/2";
		address.address2 = "Tuer 3";
		address.address3 = "Stock 5";
		address.zip = "1010";
		address.city = "Stadt";
		address.country = "AT";
		address.phone = "+43";
		return address;
	}

	private DecideAddress decideAddressAccenture() {
		final DecideAddress address = new DecideAddress();
		address.id = 100L;
		address.firstname = "Greg";
		address.lastname = "Smith";
		address.company = "SaFi GmbH";
		address.address1 = "Leerstrasse 1/2";
		address.address2 = "Accenture";
		address.address3 = "Stock 5";
		address.zip = "1010";
		address.city = "Stadt";
		address.country = "AT";
		address.phone = "+43";
		return address;
	}

	private DecideAddress decideAddressBoulevardSpain() {
		final DecideAddress address = new DecideAddress();
		address.id = 100L;
		address.firstname = "Greg";
		address.lastname = "Smith";
		address.company = "SaFi GmbH";
		address.address1 = "Boulevard";
		address.address2 = "Spain 3447";
		address.address3 = "Stock 5";
		address.zip = "1010";
		address.city = "Stadt";
		address.country = "AT";
		address.phone = "+43";
		return address;
	}

	private DecideUser decideUserFischer() {
		final DecideUser user = new DecideUser();
		user.id = 10L;
		user.firstname = "Sabine";
		user.lastname = "Fischer";
		user.mail = "s.fischer@dodax.com";
		user.username = "sfischer";
		return user;
	}

	@Test
	public void ping() {
		final Result result = controller.ping();
		assertNotNull(result);
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		final Ping ping = Json.fromJson(Json.parse(Helpers.contentAsString(result)), Ping.class);
		assertEquals("OK", ping.status);
		assertTrue(timeDiff(ping.date) < 5000);
	}

	@Test
	public void propertiesGet() {
		final Result resultFromApiCall = controller.propertiesGet();
		assertEquals(HTTP_OK, resultFromApiCall.status());
		assertEquals(CONTENT_TYPE_JSON, resultFromApiCall.contentType().get());
		assertEquals(CHARSET_UTF8, resultFromApiCall.charset().get());

		PropertiesGetResponse response = Json.fromJson(Json.parse(Helpers.contentAsString(resultFromApiCall)),
				PropertiesGetResponse.class);

		assertTrue(response.properties.size() == 2);

		assertEquals("CLEAN_DATA", response.properties.get(0).name);
		assertEquals("" + 2592000L, response.properties.get(0).value);
		assertFalse(StringUtils.isBlank(response.properties.get(0).description));

		assertEquals("ZIP_MAX_DISTANCE", response.properties.get(1).name);
		assertEquals(String.valueOf(50000), response.properties.get(1).value);
		assertFalse(StringUtils.isBlank(response.properties.get(1).description));
	}

	@Test
	public void propertyUpdate() {
		final PropertyRequest update = new PropertyRequest();
		assertNotEquals("1500", configRepository.getValue("CLEAN_DATA"));

		update.value = "1500";
		final Result updated1500 = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.propertyUpdate("CLEAN_DATA"));
		assertEquals(HTTP_OK, updated1500.status());
		assertEquals("1500", configRepository.getValue("CLEAN_DATA"));

		update.value = "200";
		final Result updated200 = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.propertyUpdate("CLEAN_DATA"));
		assertEquals(HTTP_OK, updated200.status());
		assertEquals("200", configRepository.getValue("CLEAN_DATA"));

		update.value = "199";
		final Result updated199 = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.propertyUpdate("ZIP_MAX_DISTANCE"));
		assertEquals(HTTP_OK, updated199.status());
		assertEquals("199", configRepository.getValue("ZIP_MAX_DISTANCE"));

		update.value = "500";
		final Result updated500 = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(update)),
				() -> controller.propertyUpdate("ZIP_MAX_DISTANCE"));
		assertEquals(HTTP_OK, updated500.status());
		assertEquals("500", configRepository.getValue("ZIP_MAX_DISTANCE"));
	}

	@Test
	public void countriesGet() {
		final Result resultFromApiCall = controller.countriesGet();
		assertEquals(HTTP_OK, resultFromApiCall.status());
		assertEquals(CONTENT_TYPE_JSON, resultFromApiCall.contentType().get());
		assertEquals(CHARSET_UTF8, resultFromApiCall.charset().get());
		String result = Helpers.contentAsString(resultFromApiCall);

		// testing only some name of countries
		assertTrue(result.contains("Uruguay"));
		assertTrue(result.contains("Germany"));
		assertTrue(result.contains("Austria"));
		assertTrue(result.contains("Brazil"));

		CountriesGet response = Json.fromJson(Json.parse(Helpers.contentAsString(resultFromApiCall)),
				CountriesGet.class);

		// testing number of countries
		assertEquals(NUMBER_OF_COUNTRIES, response.countries.size());

		boolean containsAustralia = false;
		boolean containsChina = false;
		boolean containsBolivia = false;
		boolean containsIreland = false;
		boolean containsUruguay = false;
		boolean containsAustria = false;
		boolean containsBrazil = false;

		// testing for code and name of some countries

		for (CountryResponse current : response.countries) {

			if (current.name.equals("Australia") && current.code.equals("AU"))
				containsAustralia = true;
			if (current.name.equals("Brazil") && current.code.equals("BR"))
				containsBrazil = true;
			if (current.name.equals("Bolivia") && current.code.equals("BO"))
				containsBolivia = true;
			if (current.name.equals("China") && current.code.equals("CN"))
				containsChina = true;
			if (current.name.equals("Ireland") && current.code.equals("IE"))
				containsIreland = true;
			if (current.name.equals("Austria") && current.code.equals("AT"))
				containsAustria = true;
			if (current.name.equals("Uruguay") && current.code.equals("UY"))
				containsUruguay = true;

		}

		assertTrue(containsAustralia && containsBolivia && containsChina && containsIreland && containsUruguay
				&& containsAustria && containsBrazil);

	}

	@Test
	public void manualReviewsGet_empty() {
		final Result result = controller.manualReviewsGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(new ManualReviewGet()), Json.parse(Helpers.contentAsString(result)));
	}

	@Test
	public void manualReviewsGetTimeFilter_empty() {
		final Result result = controller.manualReviewsGetTimeFilter(2L);
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(new ManualReviewGet()), Json.parse(Helpers.contentAsString(result)));
	}

	@Test
	public void personFiltersGet_empty() {
		final Result resultPersonList = controller.personFiltersGet();
		assertEquals(HTTP_OK, resultPersonList.status());
		assertEquals(CONTENT_TYPE_JSON, resultPersonList.contentType().get());
		assertEquals(CHARSET_UTF8, resultPersonList.charset().get());
		assertEquals(Json.toJson(new GenericFilterGet()), Json.parse(Helpers.contentAsString(resultPersonList)));
	}

	@Test
	public void companyFiltersGet_empty() {
		final Result resultCompanyList = controller.companyFiltersGet();
		assertEquals(HTTP_OK, resultCompanyList.status());
		assertEquals(CONTENT_TYPE_JSON, resultCompanyList.contentType().get());
		assertEquals(CHARSET_UTF8, resultCompanyList.charset().get());
		assertEquals(Json.toJson(new GenericFilterGet()), Json.parse(Helpers.contentAsString(resultCompanyList)));
	}

	@Test
	public void streetFiltersGet_empty() {
		final Result resultStreetList = controller.streetFiltersGet();
		assertEquals(HTTP_OK, resultStreetList.status());
		assertEquals(CONTENT_TYPE_JSON, resultStreetList.contentType().get());
		assertEquals(CHARSET_UTF8, resultStreetList.charset().get());
		assertEquals(Json.toJson(new GenericFilterGet()), Json.parse(Helpers.contentAsString(resultStreetList)));
	}

	@Test
	public void emailFiltersGet_empty() {
		final Result resultEmailList = controller.emailFiltersGet();
		assertEquals(HTTP_OK, resultEmailList.status());
		assertEquals(CONTENT_TYPE_JSON, resultEmailList.contentType().get());
		assertEquals(CHARSET_UTF8, resultEmailList.charset().get());
		assertEquals(Json.toJson(new EmailFilterGet()), Json.parse(Helpers.contentAsString(resultEmailList)));
	}

	@Test
	public void personFiltersGet_simple() {
		final PersonFilter p1 = filterRepository.savePersonFilter("Tony", "at", "1010", "Tony from Canada",
				platformAccountGroupIds);
		final PersonFilter p2 = filterRepository.savePersonFilter("Steve", "at", "1020", "desc",
				platformAccountGroupIds);
		final PersonFilter p3 = filterRepository.savePersonFilter("Monica", "at", "1030", "", platformAccountGroupIds);
		final PersonFilter p4 = filterRepository.savePersonFilter("Hector", "at", "1040", "he lives in Dublin",
				platformAccountGroupIds);

		// expected
		final GenericFilterGet expected = new GenericFilterGet();
		GenericFilterResponse g1 = new GenericFilterResponse();
		g1.id = p1.getId();
		g1.name = "tony";
		g1.country = "AT";
		g1.zip = "1010";
		g1.description = "Tony from Canada";
		g1.platformAccountGroupIds = platformAccountGroupIds;
		GenericFilterResponse g2 = new GenericFilterResponse();
		g2.id = p2.getId();
		g2.name = "steve";
		g2.country = "AT";
		g2.zip = "1020";
		g2.description = "desc";
		g2.platformAccountGroupIds = platformAccountGroupIds;
		GenericFilterResponse g3 = new GenericFilterResponse();
		g3.id = p3.getId();
		g3.name = "monica";
		g3.country = "AT";
		g3.zip = "1030";
		g3.description = null;
		g3.platformAccountGroupIds = platformAccountGroupIds;
		GenericFilterResponse g4 = new GenericFilterResponse();
		g4.id = p4.getId();
		g4.name = "hector";
		g4.country = "AT";
		g4.zip = "1040";
		g4.description = "he lives in Dublin";
		g4.platformAccountGroupIds = platformAccountGroupIds;

		expected.filters.add(g1);
		expected.filters.add(g2);
		expected.filters.add(g3);
		expected.filters.add(g4);

		// make request and compare expected to actual result
		final Result result = controller.personFiltersGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void companyFiltersGet_simple() {
		final CompanyFilter c1 = filterRepository.saveCompanyFilter("ibm", "at", "1010", null, platformAccountGroupIds);
		final CompanyFilter c2 = filterRepository.saveCompanyFilter("google", "at", "1020", null,
				platformAccountGroupIds);

		// expected
		final GenericFilterGet expected = new GenericFilterGet();
		GenericFilterResponse g1 = new GenericFilterResponse();
		g1.id = c1.getId();
		g1.name = "ibm";
		g1.country = "AT";
		g1.zip = "1010";
		g1.platformAccountGroupIds = platformAccountGroupIds;
		GenericFilterResponse g2 = new GenericFilterResponse();
		g2.id = c2.getId();
		g2.name = "google";
		g2.country = "AT";
		g2.zip = "1020";
		g2.platformAccountGroupIds = platformAccountGroupIds;

		expected.filters.add(g1);
		expected.filters.add(g2);

		// make request and compare expected to actual result
		final Result result = controller.companyFiltersGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void streetFiltersGet_simple() {
		final StreetFilter s1 = filterRepository.saveStreetFilter("mandarin avenue", "at", "1010", null,
				platformAccountGroupIds);
		final StreetFilter s2 = filterRepository.saveStreetFilter("tomas diago", "at", "1020", null,
				platformAccountGroupIds);

		// expected
		final GenericFilterGet expected = new GenericFilterGet();
		GenericFilterResponse g1 = new GenericFilterResponse();
		g1.id = s1.getId();
		g1.name = "mandarin avenue";
		g1.country = "AT";
		g1.zip = "1010";
		g1.platformAccountGroupIds = platformAccountGroupIds;
		GenericFilterResponse g2 = new GenericFilterResponse();
		g2.id = s2.getId();
		g2.name = "tomas diago";
		g2.country = "AT";
		g2.zip = "1020";
		g2.platformAccountGroupIds = platformAccountGroupIds;

		expected.filters.add(g1);
		expected.filters.add(g2);

		// make request and compare expected to actual result
		final Result result = controller.streetFiltersGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void emailFiltersGet_simple() {
		final EmailFilter e1 = filterRepository.saveEmailFilter("johnthompson@gmail.com", "description",
				platformAccountGroupIds);
		final EmailFilter e2 = filterRepository.saveEmailFilter("denissetaylor@yahoo.com", "desc",
				platformAccountGroupIds);

		// expected
		final EmailFilterGet expected = new EmailFilterGet();
		EmailFilterResponse em1 = new EmailFilterResponse();
		em1.id = e1.getId();
		em1.name = "johnthompson@gmail.com";
		em1.description = "description";
		em1.platformAccountGroupIds = platformAccountGroupIds;
		EmailFilterResponse em2 = new EmailFilterResponse();
		em2.id = e2.getId();
		em2.name = "denissetaylor@yahoo.com";
		em2.description = "desc";
		em2.platformAccountGroupIds = platformAccountGroupIds;

		expected.filters.add(em1);
		expected.filters.add(em2);

		// make request and compare expected to actual result
		final Result result = controller.emailFiltersGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void phoneFiltersGet_simple() {
		final PhoneFilter p1 = filterRepository.savePhoneFilter("1112223333", "description", platformAccountGroupIds);
		final PhoneFilter p2 = filterRepository.savePhoneFilter("4445556666", "desc", platformAccountGroupIds);

		// expected
		final PhoneFilterGet expected = new PhoneFilterGet();
		PhoneFilterResponse ep1 = new PhoneFilterResponse();
		ep1.id = p1.getId();
		ep1.name = "1112223333";
		ep1.description = "description";
		ep1.platformAccountGroupIds = platformAccountGroupIds;
		PhoneFilterResponse ep2 = new PhoneFilterResponse();
		ep2.id = p2.getId();
		ep2.name = "4445556666";
		ep2.description = "desc";
		ep2.platformAccountGroupIds = platformAccountGroupIds;

		expected.filters.add(ep1);
		expected.filters.add(ep2);

		// make request and compare expected to actual result
		final Result result = controller.phoneFiltersGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void phoneFiltersGet_empty() {
		final Result resultPhoneList = controller.phoneFiltersGet();
		assertEquals(HTTP_OK, resultPhoneList.status());
		assertEquals(CONTENT_TYPE_JSON, resultPhoneList.contentType().get());
		assertEquals(CHARSET_UTF8, resultPhoneList.charset().get());
		assertEquals(Json.toJson(new PhoneFilterGet()), Json.parse(Helpers.contentAsString(resultPhoneList)));
	}

	@Test
	public void personFiltersGet_invalidId() {
		Result resultPersonList = controller.personFilterGet(null);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
		resultPersonList = controller.personFilterGet(Long.MAX_VALUE);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
	}

	@Test
	public void comapnyFiltersGet_invalidId() {
		Result resultPersonList = controller.companyFilterGet(null);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
		resultPersonList = controller.companyFilterGet(Long.MAX_VALUE);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
	}

	@Test
	public void streetFiltersGet_invalidId() {
		Result resultPersonList = controller.streetFilterGet(null);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
		resultPersonList = controller.streetFilterGet(Long.MAX_VALUE);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
	}

	@Test
	public void phoneFiltersGet_invalidId() {
		Result resultPersonList = controller.phoneFilterGet(null);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
		resultPersonList = controller.phoneFilterGet(Long.MAX_VALUE);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
	}

	@Test
	public void emailFiltersGet_invalidId() {
		Result resultPersonList = controller.emailFilterGet(null);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
		resultPersonList = controller.emailFilterGet(Long.MAX_VALUE);
		assertEquals(HTTP_NOT_FOUND, resultPersonList.status());
	}

	@Test
	public void emailFilterGet() {
		final EmailFilter filter = filterRepository.saveEmailFilter("johnthompson@gmail.com", "description",
				platformAccountGroupIds);

		// expected
		EmailFilterResponse expected = new EmailFilterResponse();
		expected.id = filter.getId();
		expected.name = "johnthompson@gmail.com";
		expected.description = "description";
		expected.platformAccountGroupIds = platformAccountGroupIds;

		// make request and compare expected to actual result
		final Result result = controller.emailFilterGet(filter.getId());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void phoneFilterGet() {
		final PhoneFilter filter = filterRepository.savePhoneFilter("1112223333", "description",
				platformAccountGroupIds);

		// expected
		PhoneFilterResponse expected = new PhoneFilterResponse();
		expected.id = filter.getId();
		expected.name = "1112223333";
		expected.description = "description";
		expected.platformAccountGroupIds = platformAccountGroupIds;

		// make request and compare expected to actual result
		final Result result = controller.phoneFilterGet(filter.getId());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void streetFilterGet() {
		final StreetFilter filter = filterRepository.saveStreetFilter("mandarin avenue", "at", "1010", null,
				platformAccountGroupIds);

		// expected
		GenericFilterResponse expected = new GenericFilterResponse();
		expected.id = filter.getId();
		expected.name = "mandarin avenue";
		expected.country = "AT";
		expected.zip = "1010";
		expected.platformAccountGroupIds = platformAccountGroupIds;

		// make request and compare expected to actual result
		final Result result = controller.streetFilterGet(filter.getId());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void companyFilterGet() {
		final CompanyFilter filter = filterRepository.saveCompanyFilter("ibm", "at", "1010", null,
				platformAccountGroupIds);

		// expected
		GenericFilterResponse expected = new GenericFilterResponse();
		expected.id = filter.getId();
		expected.name = "ibm";
		expected.country = "AT";
		expected.zip = "1010";
		expected.platformAccountGroupIds = platformAccountGroupIds;

		// make request and compare expected to actual result
		final Result result = controller.companyFilterGet(filter.getId());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void personFilterGet() {
		final PersonFilter filter = filterRepository.savePersonFilter("Tony", "at", "1010", "Tony from Canada",
				platformAccountGroupIds);

		// expected
		GenericFilterResponse expected = new GenericFilterResponse();
		expected.id = filter.getId();
		expected.name = "tony";
		expected.country = "AT";
		expected.zip = "1010";
		expected.description = "Tony from Canada";
		expected.platformAccountGroupIds = platformAccountGroupIds;

		// make request and compare expected to actual result
		final Result result = controller.personFilterGet(filter.getId());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void testRunsGet() {
		TestCase tc1 = testRunRepository.newTestCase();
		tc1.setName("testCase1");
		tc1.setActual("true");
		tc1.setExpected("true");

		TestCase tc2 = testRunRepository.newTestCase();
		tc2.setName("testCase2");
		tc2.setActual("hello");
		tc2.setExpected("hello");

		List<TestCase> testCases = new ArrayList<>();
		testCases.add(tc1);
		testCases.add(tc2);

		TestRun toSave = testRunRepository.newTestRun();
		Date d1 = new Date();
		Date d2 = new Date();
		toSave.setStart(d1);
		toSave.setEnd(d2);
		toSave.setTestCases(testCases);

		toSave = testRunRepository.saveTestRun(toSave);

		// expected
		final TestRunsGetResponse expected = new TestRunsGetResponse();
		final TestCaseResponse tc3 = new TestCaseResponse();
		tc3.id = toSave.getTestCases().get(0).getId();
		tc3.name = "testCase1";
		tc3.expected = "true";
		tc3.actual = "true";
		tc3.result = TestResult.PASSED;

		final TestCaseResponse tc4 = new TestCaseResponse();
		tc4.id = toSave.getTestCases().get(1).getId();
		tc4.name = "testCase2";
		tc4.expected = "hello";
		tc4.actual = "hello";
		tc4.result = TestResult.PASSED;

		final TestRunResponse response = new TestRunResponse();
		response.id = toSave.getId();
		response.start = d1;
		response.end = d2;
		response.testCases = new ArrayList<TestCaseResponse>();
		response.testCases.add(tc3);
		response.testCases.add(tc4);
		response.result = TestResult.PASSED;

		List<TestRunResponse> list = new ArrayList<TestRunResponse>();
		list.add(response);
		expected.tests = list;

		// make request and compare expected to actual result
		final Result result = controller.testRuns(20);
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void manualReviewsGet_simple() {

		// create repository data
		final ReviewReason dbReasonFischer = reviewRepository.newReviewReason("susp", "fischer");
		final ReviewReason dbReasonMueller = reviewRepository.newReviewReason("susp", "mueller");
		reviewRepository.saveReviewRequest(10L, Arrays.asList(dbReasonFischer));
		reviewRepository.saveReviewRequest(20L, Arrays.asList(dbReasonMueller));
		reviewRepository.saveReviewRequest(30L, Arrays.asList(dbReasonFischer, dbReasonMueller));
		reviewRepository.saveReviewResult(10L, "ACCEPTED");
		reviewRepository.saveReviewResult(40L, "REJECTED");

		// create expectation data
		final ManualReviewGetReason reasonFischer = new ManualReviewGetReason();
		reasonFischer.id = dbReasonFischer.getId();
		reasonFischer.type = "susp";
		reasonFischer.value = "fischer";

		final ManualReviewGetReason reasonMueller = new ManualReviewGetReason();
		reasonMueller.id = dbReasonMueller.getId();
		reasonMueller.type = "susp";
		reasonMueller.value = "mueller";

		final ManualReviewGetOrder order10 = new ManualReviewGetOrder();
		order10.id = 10L;
		order10.action = "ACCEPTED";
		order10.reasons.add(dbReasonFischer.getId());

		final ManualReviewGetOrder order20 = new ManualReviewGetOrder();
		order20.id = 20L;
		order20.reasons.add(dbReasonMueller.getId());

		final ManualReviewGetOrder order30 = new ManualReviewGetOrder();
		order30.id = 30L;
		order30.reasons.add(dbReasonFischer.getId());
		order30.reasons.add(dbReasonMueller.getId());

		final ManualReviewGetOrder order40 = new ManualReviewGetOrder();
		order40.id = 40L;
		order40.action = "REJECTED";

		final ManualReviewGet expected = new ManualReviewGet();
		expected.reasons.add(reasonFischer);
		expected.reasons.add(reasonMueller);
		expected.orders.add(order10);
		expected.orders.add(order20);
		expected.orders.add(order30);
		expected.orders.add(order40);

		// make request and compare expected to actual result
		final Result result = controller.manualReviewsGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());
	}

	@Test
	public void manualReviewsGetTimeFilter_simple() {
		// create repository data

		final ReviewReason dbReasonFischer = reviewRepository.newReviewReason("susp", "fischer");
		final ReviewReason dbReasonMueller = reviewRepository.newReviewReason("susp", "mueller");
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L);
		reviewRepository.saveReviewRequest(10L, Arrays.asList(dbReasonFischer));
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-2000L);
		reviewRepository.saveReviewRequest(20L, Arrays.asList(dbReasonMueller));
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-4000L);
		reviewRepository.saveReviewRequest(30L, Arrays.asList(dbReasonFischer, dbReasonMueller));
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L);
		reviewRepository.saveReviewResult(10L, "ACCEPTED");
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-5000L);
		reviewRepository.saveReviewResult(20L, "ACCEPTED");
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-2000L);
		reviewRepository.saveReviewResult(30L, "REJECTED");
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L);
		reviewRepository.saveReviewResult(40L, "REJECTED");
		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-7000L);
		reviewRepository.saveReviewRequest(70L, Arrays.asList(dbReasonFischer, dbReasonMueller));
		reviewRepository.saveReviewResult(70L, "REJECTED");

		// assertEquals(1,reviewRepository.findReviewResultsTimeFilter(4).size());
		// create expectation data
		final ManualReviewGetReason reasonFischer = new ManualReviewGetReason();
		reasonFischer.id = dbReasonFischer.getId();
		reasonFischer.type = "susp";
		reasonFischer.value = "fischer";

		final ManualReviewGetReason reasonMueller = new ManualReviewGetReason();
		reasonMueller.id = dbReasonMueller.getId();
		reasonMueller.type = "susp";
		reasonMueller.value = "mueller";

		final ManualReviewGetOrder order10 = new ManualReviewGetOrder();
		order10.id = 10L;
		order10.action = "ACCEPTED";
		order10.reasons.add(dbReasonFischer.getId());

		final ManualReviewGetOrder order20 = new ManualReviewGetOrder();
		order20.id = 20L;
		order20.reasons.add(dbReasonMueller.getId());

		final ManualReviewGetOrder order30 = new ManualReviewGetOrder();
		order30.id = 30L;
		order30.action = "REJECTED";

		final ManualReviewGetOrder order40 = new ManualReviewGetOrder();
		order40.id = 40L;
		order40.action = "REJECTED";

		final ManualReviewGet expected = new ManualReviewGet();
		expected.reasons.add(reasonFischer);
		expected.reasons.add(reasonMueller);
		expected.orders.add(order10);
		expected.orders.add(order20);
		expected.orders.add(order30);
		expected.orders.add(order40);

		// make request and compare expected to actual result
		final Result result = controller.manualReviewsGetTimeFilter(3L);
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(expected).toString(), Json.parse(Helpers.contentAsString(result)).toString());

	}

	@Test
	public void manualReviewRequest() {
		final ManualReviewReason reason1 = new ManualReviewReason();
		reason1.type = "type1";
		reason1.value = "value1";

		final ManualReviewReason reason2 = new ManualReviewReason();
		reason2.type = "type2";
		reason2.value = "value2";

		final ManualReviewRequest body = new ManualReviewRequest();
		body.reasons = new ArrayList<>();
		body.reasons.add(reason1);
		body.reasons.add(reason2);

		assertEquals(0, reviewRepository.findAllReviewRequests().size());
		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(body)),
				() -> controller.manualReviewRequest(5544L));
		assertNotNull(result);
		assertEquals(HTTP_OK, result.status());

		assertEquals(1, reviewRepository.findAllReviewRequests().size());
		final ReviewRequest stored = reviewRepository.findAllReviewRequests().get(0);
		assertNotNull(stored.getId());
		assertEquals((Long) 5544L, stored.getOrderId());
		assertTrue(timeDiff(stored.getRequestDate()) < 5000);
		assertEquals(2, stored.getReviewReasons().size());

		final Map<String, String> storedReasons = new HashMap<>();
		for (ReviewReason cur : stored.getReviewReasons())
			storedReasons.put(cur.getType(), cur.getValue());

		assertEquals("value1", storedReasons.get("type1"));
		assertEquals("value2", storedReasons.get("type2"));
	}

	@Test
	public void manualReviewResult() {
		final ManualReviewResult body = new ManualReviewResult();
		body.action = "TestAction123";

		assertEquals(0, reviewRepository.findAllReviewResults().size());
		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(body)),
				() -> controller.manualReviewResult(5544L));
		assertNotNull(result);
		assertEquals(HTTP_OK, result.status());

		assertEquals(1, reviewRepository.findAllReviewResults().size());
		final ReviewResult stored = reviewRepository.findAllReviewResults().get(0);
		assertEquals("TestAction123", stored.getAction());
		assertTrue(timeDiff(stored.getDateAction()) < 5000);
		assertEquals((Long) 5544L, stored.getOrderId());
		assertNotNull(stored.getId());
	}

	@Test
	public void cleanManualReviewsInvalidSeconds() {
		Config.setCleanData(100L);
		assertEquals(200, controller.manualReviewsClean(111L).status());
		assertEquals(200, controller.manualReviewsClean(100L).status());

		try {
			assertEquals(200, controller.manualReviewsClean(99L).status());
			Assert.fail();
		} catch (Exception e) {
			assertTrue(e.getMessage().toLowerCase().contains("second"));
		}
	}

	@Test
	public void cleanTestRunsInvalidSeconds() {
		Config.setCleanData(100L);
		assertEquals(200, controller.testRunsClean(111L).status());
		assertEquals(200, controller.testRunsClean(100L).status());

		try {
			assertEquals(200, controller.testRunsClean(99L).status());
			Assert.fail();
		} catch (Exception e) {
			assertTrue(e.getMessage().toLowerCase().contains("second"));
		}
	}

	@Test
	public void cleanTestRunsSimple() {
		Config.setCleanData(0L);

		final TestCase tc1 = testRunRepository.newTestCase();
		tc1.setName("testCase1");
		tc1.setActual("hello");
		tc1.setExpected("hello");

		final TestCase tc2 = testRunRepository.newTestCase();
		tc2.setName("testCase2");
		tc2.setActual("999");
		tc2.setExpected("999");

		final List<TestCase> testCases = new ArrayList<>();
		testCases.add(tc1);
		testCases.add(tc2);

		final TestRun run30Days = testRunRepository.newTestRun();
		run30Days.setEnd(Utils.getDateBefore(30 * 24 * 3600));
		run30Days.setStart(new Date(run30Days.getEnd().getTime() - 1000L * 60));
		run30Days.setTestCases(testCases);
		testRunRepository.saveTestRun(run30Days);

		final TestRun run25Days = testRunRepository.newTestRun();
		run25Days.setEnd(Utils.getDateBefore(25 * 24 * 3600));
		run25Days.setStart(new Date(run25Days.getEnd().getTime() - 1000L * 60));
		run25Days.setTestCases(testCases);
		testRunRepository.saveTestRun(run25Days);

		final TestRun run20Days = testRunRepository.newTestRun();
		run20Days.setEnd(Utils.getDateBefore(20 * 24 * 3600));
		run20Days.setStart(new Date(run20Days.getEnd().getTime() - 1000L * 60));
		run20Days.setTestCases(testCases);
		testRunRepository.saveTestRun(run20Days);

		assertEquals(3, testRunRepository.getTestRuns(20).size());

		assertEquals(HTTP_OK, controller.testRunsClean(31L * 24 * 3600).status());
		assertEquals(3, testRunRepository.getTestRuns(20).size());

		assertEquals(HTTP_OK, controller.testRunsClean(29L * 24 * 3600).status());
		assertEquals(2, testRunRepository.getTestRuns(20).size());

		assertEquals(HTTP_OK, controller.testRunsClean(26L * 24 * 3600).status());
		assertEquals(2, testRunRepository.getTestRuns(20).size());

		assertEquals(HTTP_OK, controller.testRunsClean(24L * 24 * 3600).status());
		assertEquals(1, testRunRepository.getTestRuns(20).size());

		assertEquals(HTTP_OK, controller.testRunsClean(19L * 24 * 3600).status());
		assertEquals(0, testRunRepository.getTestRuns(20).size());
	}

	@Test
	public void cleanReviewsNoDelete() {
		Config.setCleanData(0L);
		final List<ReviewReason> reasons = Arrays.asList(reviewRepository.newReviewReason("type", "value"));

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 8);
		reviewRepository.saveReviewRequest(1L, reasons);
		reviewRepository.saveReviewResult(1L, "ACCEPT");

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 5);
		reviewRepository.saveReviewRequest(2L, reasons);
		reviewRepository.saveReviewResult(2L, "REJECT");

		TimeProviderFactory.FACTORY = TimeProviderFactory.factoryOffset(-1000L * 3600 * 3);
		reviewRepository.saveReviewRequest(3L, reasons);
		reviewRepository.saveReviewResult(3L, "ACCEPT");

		assertEquals(3, reviewRepository.findAllReviewRequests().size());
		assertEquals(3, reviewRepository.findAllReviewResults().size());

		assertEquals(HTTP_OK, controller.manualReviewsClean(3600L * 10).status());
		assertEquals(3, reviewRepository.findAllReviewRequests().size());
		assertEquals(3, reviewRepository.findAllReviewResults().size());

		assertEquals(HTTP_OK, controller.manualReviewsClean(3600L * 9).status());
		assertEquals(3, reviewRepository.findAllReviewRequests().size());
		assertEquals(3, reviewRepository.findAllReviewResults().size());

		assertEquals(HTTP_OK, controller.manualReviewsClean(3600L * 7).status());
		assertEquals(2, reviewRepository.findAllReviewRequests().size());
		assertEquals(2, reviewRepository.findAllReviewResults().size());

		assertEquals(HTTP_OK, controller.manualReviewsClean(3600L * 6).status());
		assertEquals(2, reviewRepository.findAllReviewRequests().size());
		assertEquals(2, reviewRepository.findAllReviewResults().size());

		assertEquals(HTTP_OK, controller.manualReviewsClean(3600L * 4).status());
		assertEquals(1, reviewRepository.findAllReviewRequests().size());
		assertEquals(1, reviewRepository.findAllReviewResults().size());

		assertEquals(HTTP_OK, controller.manualReviewsClean(3600L * 2).status());
		assertEquals(0, reviewRepository.findAllReviewRequests().size());
		assertEquals(0, reviewRepository.findAllReviewResults().size());
	}

	private static long timeDiff(Date date) {
		return Math.abs(new Date().getTime() - date.getTime());
	}

	@Test
	public void platformAccountGroupsGet_empty() {
		final Result result = controller.platformAccountGroupsGet();
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
		assertEquals(Json.toJson(new PlatformAccountGroupGetResponse()), Json.parse(Helpers.contentAsString(result)));
	}

	@Test
	public void platformAccountGroupsGet() {

		String[] accounts = { "acct1" };
		platformAccountGroupRepository.savePlatformAccountGroup("ukPlatformGrp", "test", accounts);

		final Result actual = controller.platformAccountGroupsGet();

		// expected
		PlatformAccountGroupGetResponse expected = new PlatformAccountGroupGetResponse();
		PlatformAccountGroupResponse data = new PlatformAccountGroupResponse();
		data.id = 2L;
		data.name = "ukPlatformGrp";
		data.description = "test";
		data.accounts = accounts;
		expected.response.add(data);

		assertEquals(HTTP_OK, actual.status());
		assertEquals(CONTENT_TYPE_JSON, actual.contentType().get());
		assertEquals(CHARSET_UTF8, actual.charset().get());
		assertEquals(Json.toJson(expected).toString(), Helpers.contentAsString(actual));
	}

	@Test
	public void platformAccountGroupGet_InvalidId() {
		Result result = controller.platformAccountGroupGet(null);
		assertEquals(HTTP_NOT_FOUND, result.status());

		result = controller.platformAccountGroupGet(Long.MAX_VALUE);
		assertEquals(HTTP_NOT_FOUND, result.status());
	}

	@Test
	public void platformAccountGroupGet() {

		String[] accounts = { "acct1" };
		PlatformAccountGroup group = platformAccountGroupRepository.savePlatformAccountGroup("ukPlatformGrp", "test",
				accounts);

		final Result actual = controller.platformAccountGroupGet(group.getId());

		// expected
		PlatformAccountGroupResponse expected = new PlatformAccountGroupResponse();
		expected.id = group.getId();
		expected.name = "ukPlatformGrp";
		expected.description = "test";
		expected.accounts = accounts;

		assertEquals(HTTP_OK, actual.status());
		assertEquals(CONTENT_TYPE_JSON, actual.contentType().get());
		assertEquals(CHARSET_UTF8, actual.charset().get());
		assertEquals(Json.toJson(expected).toString(), Helpers.contentAsString(actual));
	}

	@Test
	public void platformAccountGroupsSave() {
		assertEquals(0, platformAccountGroupRepository.findAllPlatformAccountGroups().size());
		final PlatformAccountGroupRequest request = new PlatformAccountGroupRequest();
		request.name = "ukPlatformGrp";
		request.description = "test";
		request.accounts = new String[] { "acct1" };

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.platformAccountGroupsSave());
		assertEquals(HTTP_OK, result.status());
		assertEquals(1, platformAccountGroupRepository.findAllPlatformAccountGroups().size());
		assertEquals("ukPlatformGrp", platformAccountGroupRepository.findAllPlatformAccountGroups().get(0).getName());
		assertEquals("test", platformAccountGroupRepository.findAllPlatformAccountGroups().get(0).getDescription());
	}

	@Test
	public void platformAccountGroupsDelete() {

		PlatformAccountGroup group = platformAccountGroupRepository.savePlatformAccountGroup("ukPlatformGrp", "test",
				new String[] { "acct1" });

		final Result result = controller.platformAccountGroupsDelete(group.getId());
		assertEquals(HTTP_OK, result.status());
		assertEquals(0, platformAccountGroupRepository.findAllPlatformAccountGroups().size());
	}

	@Test
	public void filterHistoryGetWithWrongFilterId() {

		final Result actual = controller.filterHistoriesGet(1L);

		// expected
		FilterHistoriesGet expected = new FilterHistoriesGet();

		assertEquals(HTTP_OK, actual.status());
		assertEquals(CONTENT_TYPE_JSON, actual.contentType().get());
		assertEquals(CHARSET_UTF8, actual.charset().get());
		assertEquals(Json.toJson(expected).toString(), Helpers.contentAsString(actual));
	}

	@Test
	public void companyFilterHistoryGet() {

		CompanyFilter filter = filterRepository.saveCompanyFilter("Facebook", "IR", null, "", platformAccountGroupIds);

		final Result actual = controller.filterHistoriesGet(filter.getId());

		// expected
		FilterHistoryResponse data = new FilterHistoryResponse();
		data.filterId = filter.getId();
		data.name = filter.getName();
		data.type = "COMPANY";
		data.action = "ADD";

		FilterHistoriesGet expected = new FilterHistoriesGet();
		expected.filterHistoryResponses = Arrays.asList(data);

		assertFilterHistory(actual, expected);
	}

	@Test
	public void emailFilterHistoryGet() {

		EmailFilter filter = filterRepository.saveEmailFilter("first@email.com", null, platformAccountGroupIds);

		final Result actual = controller.filterHistoriesGet(filter.getId());

		// expected
		FilterHistoryResponse data = new FilterHistoryResponse();
		data.filterId = filter.getId();
		data.name = filter.getName();
		data.type = "EMAIL";
		data.action = "ADD";

		FilterHistoriesGet expected = new FilterHistoriesGet();
		expected.filterHistoryResponses = Arrays.asList(data);

		assertFilterHistory(actual, expected);
	}

	@Test
	public void personFilterHistoryGet() {

		PersonFilter filter = filterRepository.savePersonFilter("John Erikson", "at", "1010", "",
				platformAccountGroupIds);

		final Result actual = controller.filterHistoriesGet(filter.getId());

		// expected
		FilterHistoryResponse data = new FilterHistoryResponse();
		data.filterId = filter.getId();
		data.name = filter.getName();
		data.type = "PERSON";
		data.action = "ADD";

		FilterHistoriesGet expected = new FilterHistoriesGet();
		expected.filterHistoryResponses = Arrays.asList(data);

		assertFilterHistory(actual, expected);
	}

	@Test
	public void streetFilterHistoryGet() {

		StreetFilter filter = filterRepository.saveStreetFilter("Madison Avenue", "US", null, "",
				platformAccountGroupIds);

		final Result actual = controller.filterHistoriesGet(filter.getId());

		// expected
		FilterHistoryResponse data = new FilterHistoryResponse();
		data.filterId = filter.getId();
		data.name = filter.getName();
		data.type = "STREET";
		data.action = "ADD";

		FilterHistoriesGet expected = new FilterHistoriesGet();
		expected.filterHistoryResponses = Arrays.asList(data);

		assertFilterHistory(actual, expected);
	}

	@Test
	public void zipCodesSave() {
		ZipCode zipCode = zipCodeRepository.getZipCode("at", "1060");
		assertNull(zipCode);
		final ZipCodeRequest request = new ZipCodeRequest();
		request.zip = "1060";
		request.countryCode = "AT";
		request.latitude = "48.1952";
		request.longitude = "16.3503";
		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.zipCodesSave());
		assertEquals(HTTP_OK, result.status());

		zipCode = zipCodeRepository.getZipCode("AT", "1060");

		assertNotNull(zipCode);
		assertEquals("1060", zipCode.getZip());
		assertEquals("AT", zipCode.getCountryCode());
		assertEquals("1060", zipCode.getZip());
		assertEquals("48.1952", zipCode.getLatitude());
		assertEquals("16.3503", zipCode.getLongitude());

		// test response
		final ZipCodeResponse response = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				ZipCodeResponse.class);
		assertEquals(response.zip, request.zip);
		assertEquals(request.countryCode, request.countryCode);
		assertEquals(request.latitude, request.latitude);
		assertEquals(request.longitude, request.longitude);
	}

	@Test
	public void phoneFilterHistoryGet() {

		PhoneFilter filter = filterRepository.savePhoneFilter("+43 111 222 3333", "", platformAccountGroupIds);

		final Result actual = controller.filterHistoriesGet(filter.getId());

		// expected
		FilterHistoryResponse data = new FilterHistoryResponse();
		data.filterId = filter.getId();
		data.name = filter.getName();
		data.type = "PHONE";
		data.action = "ADD";

		FilterHistoriesGet expected = new FilterHistoriesGet();
		expected.filterHistoryResponses = Arrays.asList(data);

		assertFilterHistory(actual, expected);
	}

	// test cases to test /decide with zipcode distance
	@Test
	public void decideZipCodeDistanceMatch_testCase1() {
		filterRepository.savePersonFilter("dominik", "AT", "1220", null, null);

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "dominik";
		request.user = user;
		final DecideAddress address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "3300";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.ACCEPT, decideResult.order.decision);
		assertEquals(0, decideResult.order.reasons.size());

	}

	@Test
	public void decideZipCodeDistanceMatch_testCase2() {
		PersonFilter filter = filterRepository.savePersonFilter("dominik", "AT", "1220", null, null);

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "string";
		request.user = user;
		final DecideAddress address = new DecideAddress();
		address.id = 0L;
		address.firstname = "dominik";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "1220";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
	}

	@Test
	public void decideZipCodeDistanceMatch_testCase3() {
		filterRepository.savePersonFilter("dominik", "AT", "1220", null, null);

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "string";
		request.user = user;
		final DecideAddress address = new DecideAddress();
		address.id = 0L;
		address.firstname = "dominik";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "3300";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.ACCEPT, decideResult.order.decision);
		assertEquals(0, decideResult.order.reasons.size());
	}

	@Test
	public void decideZipCodeDistanceMatch_testCase4() {
		PersonFilter filter = filterRepository.savePersonFilter("dominik", "AT", "1220", null, null);

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "string";
		request.user = user;
		final DecideAddress address = new DecideAddress();
		address.id = 0L;
		address.firstname = "dominik";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "1010";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
	}

	@Test
	public void decideZipCodeDistanceMatch_testCase5() {
		PersonFilter filter = filterRepository.savePersonFilter("dominik", "AT", "1220", null, null);

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "dominik";
		request.user = user;
		final DecideAddress address1 = new DecideAddress();
		address1.id = 0L;
		address1.firstname = "string";
		address1.lastname = "string";
		address1.company = "string";
		address1.address1 = "string";
		address1.address2 = "string";
		address1.address3 = "string";
		address1.zip = "3300";
		address1.country = "AT";

		final DecideAddress address2 = new DecideAddress();
		address2.id = 1L;
		address2.firstname = "string";
		address2.lastname = "string";
		address2.company = "string";
		address2.address1 = "string";
		address2.address2 = "string";
		address2.address3 = "string";
		address2.zip = "1010";
		address2.country = "AT";
		request.addresses = Arrays.asList(address1, address2);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
	}

	@Test
	public void decideZipCodeDistanceMatch_testCase6() {
		PersonFilter filter = filterRepository.savePersonFilter("mark", null, null, null, null);

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "mark";
		request.user = user;

		// a) no country zipcode
		DecideAddress address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = null;
		address.country = null;
		request.addresses = Arrays.asList(address);

		Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		// b) no country code but has zip
		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "1010";
		address.country = null;
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		// c) has country code but no zip
		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = null;
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		// d) has country code but no zip
		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "1010";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
	}

	@Test
	public void decideZipCodeDistanceMatch_testCase7() {
		// this zipcode/country code does not exit in testcodes table
		PersonFilter filter = filterRepository.savePersonFilter("mark", "AT", "3800", null, null);

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "mark";
		request.user = user;

		// a) no country zipcode
		DecideAddress address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = null;
		address.country = null;
		request.addresses = Arrays.asList(address);

		Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		// b) no country code but has zip
		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "1010";
		address.country = null;
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		// c) has country code but no zip
		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = null;
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		// d) has country code but no zip
		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "1010";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
	}

	@Test
	public void decideZipCodeDistanceMatch_testCase8() {

		// create a filter with zip value not matching in zipcodes table, but
		// can match after stripping trailing digit with "0"
		PersonFilter filter = filterRepository.savePersonFilter("mark", "AT", "1011", null, null);

		// a) /decide API sends non-numeric zip/country code, but numeric
		// zip/country code with exists in zipcodes table

		final DecideRequest request = new DecideRequest();
		DecideUser user = new DecideUser();
		user.id = 0L;
		user.firstname = "mark";
		request.user = user;

		DecideAddress address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "D-1020";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "D+1020";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));

		// b) /decide API sends numeric zip/country code but match exists only
		// after stripping trailing digit by "0"

		address = new DecideAddress();
		address.id = 0L;
		address.firstname = "string";
		address.lastname = "string";
		address.company = "string";
		address.address1 = "string";
		address.address2 = "string";
		address.address3 = "string";
		address.zip = "1021";
		address.country = "AT";
		request.addresses = Arrays.asList(address);

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)), DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(1, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
	}

	@Test
	public void zipCodesGet_valid() {

		final Result resultFromApiCall = controller.zipCodesGet("AT", "1010");
		assertEquals(HTTP_OK, resultFromApiCall.status());
		assertEquals(CONTENT_TYPE_JSON, resultFromApiCall.contentType().get());
		assertEquals(CHARSET_UTF8, resultFromApiCall.charset().get());
		final ZipCodesGet zipCodesGet = Json.fromJson(Json.parse(Helpers.contentAsString(resultFromApiCall)),
				ZipCodesGet.class);
		assertNotNull(zipCodesGet.response);
		assertEquals("AT", zipCodesGet.response.countryCode);
		assertEquals("1010", zipCodesGet.response.zip);
		assertNotNull(zipCodesGet.response.latitude);
		assertNotNull(zipCodesGet.response.longitude);
	}

	@Test
	public void zipCodesGet_inValid() {

		final Result resultFromApiCall = controller.zipCodesGet("AA", "1234");
		assertEquals(HTTP_OK, resultFromApiCall.status());
		assertEquals(CONTENT_TYPE_JSON, resultFromApiCall.contentType().get());
		assertEquals(CHARSET_UTF8, resultFromApiCall.charset().get());
		final ZipCodesGet zipCodesGet = Json.fromJson(Json.parse(Helpers.contentAsString(resultFromApiCall)),
				ZipCodesGet.class);
		assertNull(zipCodesGet.response);
	}

	@Test
	public void allFiltersSave() {
		assertEquals(0, filterRepository.findAllFilters().size());
		final AllFiltersSaveRequest request = new AllFiltersSaveRequest();
		request.companyName = "fischer";
		request.emailName = "john_smith@gmail.com";
		request.personName = "muller";
		request.phoneName = "6609049071";
		request.streetName = "boulevard spain";
		request.country = "AT";
		request.zip = "1010";
		request.description = "desc123";
		request.platformAccountGroupIds = new Long[] {
			TestPlatformAccountGroupCreator.createPlatformAccountGroup().getId() };

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.allFiltersSave());

		assertEquals(HTTP_OK, result.status());
		assertEquals(5, filterRepository.findAllFilters().size());

		assertEquals("fischer", filterRepository.findAllCompanyFilters().get(0).getName());
		assertEquals("AT", filterRepository.findAllCompanyFilters().get(0).getCountry());
		assertEquals("1010", filterRepository.findAllCompanyFilters().get(0).getZip());
		assertEquals("desc123", filterRepository.findAllCompanyFilters().get(0).getDescription());
		assertEquals(request.platformAccountGroupIds[0],
				filterRepository.findAllCompanyFilters().get(0).getPlatformAccountGroupIds()[0]);

		assertEquals("muller", filterRepository.findAllPersonFilters().get(0).getName());
		assertEquals("AT", filterRepository.findAllPersonFilters().get(0).getCountry());
		assertEquals("1010", filterRepository.findAllPersonFilters().get(0).getZip());
		assertEquals("desc123", filterRepository.findAllPersonFilters().get(0).getDescription());
		assertEquals(request.platformAccountGroupIds[0],
				filterRepository.findAllPersonFilters().get(0).getPlatformAccountGroupIds()[0]);

		assertEquals("boulevard spain", filterRepository.findAllStreetFilters().get(0).getName());
		assertEquals("AT", filterRepository.findAllStreetFilters().get(0).getCountry());
		assertEquals("1010", filterRepository.findAllStreetFilters().get(0).getZip());
		assertEquals("desc123", filterRepository.findAllStreetFilters().get(0).getDescription());
		assertEquals(request.platformAccountGroupIds[0],
				filterRepository.findAllStreetFilters().get(0).getPlatformAccountGroupIds()[0]);

		assertEquals("john_smith@gmail.com", filterRepository.findAllEmailFilters().get(0).getName());
		assertEquals("desc123", filterRepository.findAllEmailFilters().get(0).getDescription());
		assertEquals(request.platformAccountGroupIds[0],
				filterRepository.findAllEmailFilters().get(0).getPlatformAccountGroupIds()[0]);

		assertEquals("6609049071", filterRepository.findAllPhoneFilters().get(0).getName());
		assertEquals("desc123", filterRepository.findAllPhoneFilters().get(0).getDescription());
		assertEquals(request.platformAccountGroupIds[0],
				filterRepository.findAllPhoneFilters().get(0).getPlatformAccountGroupIds()[0]);

		// test response
		final AllFiltersSaveResponse filter = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				AllFiltersSaveResponse.class);

		assertEquals(filterRepository.findAllCompanyFilters().get(0).getId(), filter.companyFilterResponse.id);
		assertEquals(request.companyName, filter.companyFilterResponse.name);
		assertEquals(request.country, filter.companyFilterResponse.country);
		assertEquals(request.zip, filter.companyFilterResponse.zip);
		assertEquals(request.description, filter.companyFilterResponse.description);
		assertEquals(request.platformAccountGroupIds[0], filter.companyFilterResponse.platformAccountGroupIds[0]);

		assertEquals(filterRepository.findAllPersonFilters().get(0).getId(), filter.personFilterResponse.id);
		assertEquals(request.personName, filter.personFilterResponse.name);
		assertEquals(request.country, filter.personFilterResponse.country);
		assertEquals(request.zip, filter.personFilterResponse.zip);
		assertEquals(request.description, filter.personFilterResponse.description);
		assertEquals(request.platformAccountGroupIds[0], filter.personFilterResponse.platformAccountGroupIds[0]);

		assertEquals(filterRepository.findAllStreetFilters().get(0).getId(), filter.streetFilterResponse.id);
		assertEquals(request.streetName, filter.streetFilterResponse.name);
		assertEquals(request.country, filter.streetFilterResponse.country);
		assertEquals(request.zip, filter.streetFilterResponse.zip);
		assertEquals(request.description, filter.streetFilterResponse.description);
		assertEquals(request.platformAccountGroupIds[0], filter.streetFilterResponse.platformAccountGroupIds[0]);

		assertEquals(filterRepository.findAllEmailFilters().get(0).getId(), filter.emailFilterResponse.id);
		assertEquals(request.emailName, filter.emailFilterResponse.name);
		assertEquals(request.description, filter.emailFilterResponse.description);
		assertEquals(request.platformAccountGroupIds[0], filter.emailFilterResponse.platformAccountGroupIds[0]);

		assertEquals(filterRepository.findAllPhoneFilters().get(0).getId(), filter.phoneFilterResponse.id);
		assertEquals(request.phoneName, filter.phoneFilterResponse.name);
		assertEquals(request.description, filter.phoneFilterResponse.description);
		assertEquals(request.platformAccountGroupIds[0], filter.phoneFilterResponse.platformAccountGroupIds[0]);
	}

	@Test
	public void allFiltersSave_partial() {
		assertEquals(0, filterRepository.findAllFilters().size());

		Long groupId = TestPlatformAccountGroupCreator.createPlatformAccountGroup().getId();

		// add company filter
		AllFiltersSaveRequest request = new AllFiltersSaveRequest();
		request.companyName = "fischer";
		request.country = "AT";
		request.zip = "1010";
		request.description = "desc123";
		request.platformAccountGroupIds = new Long[] { groupId };

		Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.allFiltersSave());

		assertEquals(HTTP_OK, result.status());
		assertEquals(1, filterRepository.findAllFilters().size());

		assertEquals(1, filterRepository.findAllCompanyFilters().size());
		assertEquals(0, filterRepository.findAllPersonFilters().size());
		assertEquals(0, filterRepository.findAllStreetFilters().size());
		assertEquals(0, filterRepository.findAllEmailFilters().size());
		assertEquals(0, filterRepository.findAllPhoneFilters().size());

		// add person filter
		request = new AllFiltersSaveRequest();
		request.personName = "muller";
		request.country = "AT";
		request.zip = "1010";
		request.description = "desc123";
		request.platformAccountGroupIds = new Long[] { groupId };

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.allFiltersSave());

		assertEquals(HTTP_OK, result.status());
		assertEquals(2, filterRepository.findAllFilters().size());

		assertEquals(1, filterRepository.findAllCompanyFilters().size());
		assertEquals(1, filterRepository.findAllPersonFilters().size());
		assertEquals(0, filterRepository.findAllStreetFilters().size());
		assertEquals(0, filterRepository.findAllEmailFilters().size());
		assertEquals(0, filterRepository.findAllPhoneFilters().size());

		// add email filter
		request = new AllFiltersSaveRequest();
		request.emailName = "john_smith@gmail.com";
		request.description = "desc123";
		request.platformAccountGroupIds = new Long[] { groupId };

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.allFiltersSave());

		assertEquals(HTTP_OK, result.status());
		assertEquals(3, filterRepository.findAllFilters().size());

		assertEquals(1, filterRepository.findAllCompanyFilters().size());
		assertEquals(1, filterRepository.findAllPersonFilters().size());
		assertEquals(1, filterRepository.findAllEmailFilters().size());
		assertEquals(0, filterRepository.findAllStreetFilters().size());
		assertEquals(0, filterRepository.findAllPhoneFilters().size());

		// add street filter
		request = new AllFiltersSaveRequest();
		request.streetName = "boulevard spain";
		request.country = "AT";
		request.zip = "1010";
		request.description = "desc123";
		request.platformAccountGroupIds = new Long[] { groupId };

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.allFiltersSave());

		assertEquals(HTTP_OK, result.status());
		assertEquals(4, filterRepository.findAllFilters().size());

		assertEquals(1, filterRepository.findAllCompanyFilters().size());
		assertEquals(1, filterRepository.findAllPersonFilters().size());
		assertEquals(1, filterRepository.findAllEmailFilters().size());
		assertEquals(1, filterRepository.findAllStreetFilters().size());
		assertEquals(0, filterRepository.findAllPhoneFilters().size());

		// add phone filter
		request = new AllFiltersSaveRequest();
		request.phoneName = "6609049071";
		request.description = "desc123";
		request.platformAccountGroupIds = new Long[] { groupId };

		result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.allFiltersSave());

		assertEquals(HTTP_OK, result.status());
		assertEquals(5, filterRepository.findAllFilters().size());

		assertEquals(1, filterRepository.findAllCompanyFilters().size());
		assertEquals(1, filterRepository.findAllPersonFilters().size());
		assertEquals(1, filterRepository.findAllEmailFilters().size());
		assertEquals(1, filterRepository.findAllStreetFilters().size());
		assertEquals(1, filterRepository.findAllPhoneFilters().size());

	}

	@Test(expected = RuntimeException.class)
	public void allFiltersSaveFail() {
		assertEquals(0, filterRepository.findAllFilters().size());
		final AllFiltersSaveRequest request = new AllFiltersSaveRequest();
		request.companyName = "fischer";
		request.emailName = "wrong value provided";
		request.personName = "muller";
		request.phoneName = "6609049071";
		request.streetName = "boulevard spain";
		request.country = "AT";
		request.zip = "1010";
		request.description = "desc123";
		request.platformAccountGroupIds = new Long[] {
			TestPlatformAccountGroupCreator.createPlatformAccountGroup().getId() };

		Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.allFiltersSave());
	}

	@Test
	public void saveTestDataSet_realData() {

		testDataSetRepository.saveTestDataSet(1000L, new long[] { 10 }, new long[] { 20 }, new long[] { 30, 40, 50 });

		TestDataSetRequest request = new TestDataSetRequest();
		request.expectAccept = EXPECT_ACCEPT_DATA;
		request.expectReview = EXPECT_REVIEW_DATA;
		request.performance = PERFORMANCE_DATA;
		request.timeoutMilliseconds = TIME_OUT_IN_MS;

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.testDataSetSave());

		assertEquals(HTTP_OK, result.status());

		// test response
		final TestDataSetResponse response = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				TestDataSetResponse.class);

		assertEquals(TIME_OUT_IN_MS, response.timeoutMilliseconds);
		assertEquals(EXPECT_ACCEPT_DATA.length, response.expectAccept.length);
		assertArrayEquals(EXPECT_ACCEPT_DATA, response.expectAccept);
		assertArrayEquals(EXPECT_REVIEW_DATA, response.expectReview);
		assertArrayEquals(PERFORMANCE_DATA, response.performance);
	}

	@Test
	public void getTestDataSet_realData() {

		testDataSetRepository.saveTestDataSet(TIME_OUT_IN_MS, EXPECT_ACCEPT_DATA, EXPECT_REVIEW_DATA, PERFORMANCE_DATA);

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest(), () -> controller.testDataSetGet());

		assertEquals(HTTP_OK, result.status());

		// test response
		final TestDataSetResponse response = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				TestDataSetResponse.class);

		assertEquals(TIME_OUT_IN_MS, response.timeoutMilliseconds);
		assertEquals(EXPECT_ACCEPT_DATA.length, response.expectAccept.length);
		assertArrayEquals(EXPECT_ACCEPT_DATA, response.expectAccept);
		assertArrayEquals(EXPECT_REVIEW_DATA, response.expectReview);
		assertArrayEquals(PERFORMANCE_DATA, response.performance);
  }
  
	public void decideMatchWithPlatformAccountIdHavingSpaces() {
		String[] platformAccountId = { "amazon.uk@dodax.com" };
		PlatformAccountGroup platformAccountGroup = platformAccountGroupRepository
				.savePlatformAccountGroup("ukPlatforms", "desc", platformAccountId);
		PersonFilter filter = filterRepository.savePersonFilter("sabine", null, null, null,
				new Long[] { platformAccountGroup.getId() });

		final DecideRequest request = new DecideRequest();
		request.user = decideUserFischer();
		request.addresses = Arrays.asList(decideAddressFischer());
		// add space and some tabs
		request.platformAccountId = " \t\t" + platformAccountId[0] + "  \t\t";

		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest().bodyJson(Json.toJson(request)),
				() -> controller.decide());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		final DecideResult decideResult = Json.fromJson(Json.parse(Helpers.contentAsString(result)),
				DecideResult.class);
		assertEquals(DecideOutcome.REVIEW, decideResult.order.decision);
		assertEquals(2, decideResult.order.reasons.size());
		assertTrue(decideResult.order.reasons.get(0).message.contains(filter.getName()));
		assertTrue(decideResult.order.reasons.get(1).message.contains(filter.getName()));
	}

	private void assertFilterHistory(final Result result, FilterHistoriesGet expected) {
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_JSON, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());

		FilterHistoriesGet actual = new Gson().fromJson(Helpers.contentAsString(result), FilterHistoriesGet.class);

		assertEquals(expected.filterHistoryResponses.get(0).name, actual.filterHistoryResponses.get(0).name);
		assertEquals(expected.filterHistoryResponses.get(0).filterId, actual.filterHistoryResponses.get(0).filterId);
		assertEquals(expected.filterHistoryResponses.get(0).action, actual.filterHistoryResponses.get(0).action);
		assertEquals(expected.filterHistoryResponses.get(0).type, actual.filterHistoryResponses.get(0).type);
		assertNotNull(actual.filterHistoryResponses.get(0).modified);
		assertEquals("user", actual.filterHistoryResponses.get(0).modifiedBy);
	}

	private void createTestZipCodes() {
		TestZipCodeCreator.createZipCode("1010", "Wien", "AT", "48.2077", "16.3705");
		TestZipCodeCreator.createZipCode("1020", "Wien", "AT", "48.2167", "16.4000");
		TestZipCodeCreator.createZipCode("1030", "Wien", "AT", "48.1981", "16.3948");
		TestZipCodeCreator.createZipCode("1040", "Wien", "AT", "48.192", "16.3671");
		TestZipCodeCreator.createZipCode("1050", "Wien", "AT", "48.1865", "16.3549");
		TestZipCodeCreator.createZipCode("9992", "Iselsberg-Stronach", "AT", "46.8357", "12.8497");
		TestZipCodeCreator.createZipCode("1220", "Wien", "AT", "48.22564", "16.4997");
		TestZipCodeCreator.createZipCode("3300", "Winklarn", "AT", "48.0833", "14.8333");
		TestZipCodeCreator.createZipCode("9990", "Nußdorf-Debant", "AT", "48.4667", "12.7721");
	}

}
