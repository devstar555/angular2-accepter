package controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.stream.javadsl.Source;
import akka.util.ByteString;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import play.http.HttpEntity;
import play.libs.ws.StreamedResponse;
import play.libs.ws.WSAuthScheme;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponseHeaders;
import play.mvc.Controller;
import play.mvc.Result;

public class ChartController extends Controller {

	static {
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	private static final ChartOptionValue OPTION_INSTANCE_CH = new ChartOptionValue("com.dodax", "CH");
	private static final ChartOptionValue OPTION_INSTANCE_DE = new ChartOptionValue("de.dodax", "DE");
	private static final ChartOption OPTION_INSTANCE = new ChartOption("Instance", "instance", OPTION_INSTANCE_CH,
			OPTION_INSTANCE_CH, OPTION_INSTANCE_DE);

	private static final ChartOptionValue OPTION_TYPE_COUNT = new ChartOptionValue(".count", "Count");
	private static final ChartOptionValue OPTION_TYPE_FRACTION = new ChartOptionValue(".fraction", "Fraction");
	private static final ChartOption OPTION_TYPE = new ChartOption("Type", "type", OPTION_TYPE_FRACTION,
			OPTION_TYPE_COUNT, OPTION_TYPE_FRACTION);

	private static final ChartOptionValue OPTION_WINDOW_0W1W = new ChartOptionValue(".between0And1Weeks",
			"Last Week (-1w, now)");
	private static final ChartOptionValue OPTION_WINDOW_1W2W = new ChartOptionValue(".between1And2Weeks",
			"One Week (-2w, -1w)");
	private static final ChartOptionValue OPTION_WINDOW_1W5W = new ChartOptionValue(".between1And5Weeks",
			"One Month (-5w, -1w)");
	private static final ChartOptionValue OPTION_WINDOW_1W24W = new ChartOptionValue(".between1And24Weeks",
			"Half Year (-24w, -1w)");
	private static final ChartOption OPTION_WINDOW = new ChartOption("Window", "window", OPTION_WINDOW_0W1W,
			OPTION_WINDOW_0W1W, OPTION_WINDOW_1W2W, OPTION_WINDOW_1W5W, OPTION_WINDOW_1W24W);

	private static final ChartOptionValue OPTION_HISTORY_1D = new ChartOptionValue("1", "1 Day");
	private static final ChartOptionValue OPTION_HISTORY_3D = new ChartOptionValue("3", "3 Day");
	private static final ChartOptionValue OPTION_HISTORY_7D = new ChartOptionValue("7", "1 Week");
	private static final ChartOptionValue OPTION_HISTORY_14D = new ChartOptionValue("14", "2 Week");
	private static final ChartOptionValue OPTION_HISTORY_21D = new ChartOptionValue("21", "3 Week");
	private static final ChartOptionValue OPTION_HISTORY_30D = new ChartOptionValue("30", "1 Month");
	private static final ChartOptionValue OPTION_HISTORY_90D = new ChartOptionValue("90", "3 Month");
	private static final ChartOptionValue OPTION_HISTORY_180D = new ChartOptionValue("180", "6 Month");
	private static final ChartOptionValue OPTION_HISTORY_360D = new ChartOptionValue("360", "1 Year");
	private static final ChartOption OPTION_HISTORY = new ChartOption("History", "history", OPTION_HISTORY_30D,
			OPTION_HISTORY_1D, OPTION_HISTORY_3D, OPTION_HISTORY_7D, OPTION_HISTORY_14D, OPTION_HISTORY_21D,
			OPTION_HISTORY_30D, OPTION_HISTORY_90D, OPTION_HISTORY_180D, OPTION_HISTORY_360D);

	private static final Chart CHART_ALL = new Chart("", "All Orders", false);
	private static final Chart CHART_AUTO = new Chart(".auto", "No Review");
	private static final Chart CHART_MANUAL = new Chart(".manual", "Manual Review");
	private static final Chart CHART_MANUAL_PENDING = new Chart(".manual.pending", "Decision PENDING");
	private static final Chart CHART_MANUAL_ACCEPTED = new Chart(".manual.accepted", "Decision ACCEPTED");
	private static final Chart CHART_MANUAL_DELEGATED = new Chart(".manual.delegated", "Decision DELEGATED");
	private static final Chart CHART_MANUAL_REJECTED = new Chart(".manual.rejected", "Decision REJECTED");

	private static final ChartGroup GROUP_ALL = new ChartGroup("Orders", CHART_ALL);
	private static final ChartGroup GROUP_AUTOMANUAL = new ChartGroup("Routing", CHART_MANUAL, CHART_AUTO);
	private static final ChartGroup GROUP_DECISIONS = new ChartGroup("Decisions", CHART_MANUAL_ACCEPTED,
			CHART_MANUAL_DELEGATED, CHART_MANUAL_REJECTED, CHART_MANUAL_PENDING);

	private static final String URL_BASE = "/bartizan?key=";
	private static final String URL_CHART = ".backend.orders.accept";
	private static final String URL_CUSTOMIZATION = "&continous=true&legendPosition=none&height=190&historyWindow=true";
	private static final String URL_PARAM_TITLE = "&title=";
	private static final String URL_PARAM_HISTORY = "&history=";
	private Config config = ConfigFactory.load();

	@Inject
	WSClient ws;

	String baseUrl = config.getString("bartizan.server.url");
	String username = config.getString("bartizan.server.username");
	String password = config.getString("bartizan.server.password");
	String timeout = config.getString("bartizan.server.request.timeout");

	public Result index() {
		return ok(views.html.chart.render(this));
	}

	@ApiResponses({
			@ApiResponse(code = 200, message = "bartizan server Response", response = String.class) })
	public CompletionStage<Result> bartizan() throws IOException {

		WSRequest wsRequest = ws.url(baseUrl).setRequestTimeout(Long.valueOf(timeout))
				.setAuth(username, password, WSAuthScheme.BASIC).setMethod("GET");

		request().queryString().entrySet().stream()
				.forEach(entry -> wsRequest.setQueryParameter(entry.getKey(), entry.getValue()[0]));

		CompletionStage<StreamedResponse> futureResponse = wsRequest.stream();

		return futureResponse.thenApply(response -> {
			WSResponseHeaders responseHeaders = response.getHeaders();
			Source<ByteString, ?> body = response.getBody();
			
			if (responseHeaders.getStatus() == 200) {

				String contentType = Optional.ofNullable(responseHeaders.getHeaders().get("Content-Type"))
						.map(contentTypes -> contentTypes.get(0)).orElse("application/octet-stream");

				
				Optional<String> contentLength = Optional.ofNullable(responseHeaders.getHeaders().get("Content-Length"))
						.map(contentLengths -> contentLengths.get(0));
				if (contentLength.isPresent()) {
					return ok().sendEntity(new HttpEntity.Streamed(body,
							Optional.of(Long.parseLong(contentLength.get())), Optional.of(contentType)));
				} else {
					return ok().chunked(body).as(contentType);
				}
			} else {
				return new Result(responseHeaders.getStatus());
			}
		});
	}

	public List<ChartGroup> groups() {
		return Arrays.asList(GROUP_ALL, GROUP_AUTOMANUAL, GROUP_DECISIONS);
	}

	public List<ChartOption> options() {
		return Arrays.asList(OPTION_INSTANCE, OPTION_TYPE, OPTION_WINDOW, OPTION_HISTORY);
	}

	private static String urlEncode(String value) {
		try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static class Chart {
		private final String value;
		private final String label;
		private final boolean supportsFraction;

		public Chart(String value, String label) {
			this(value, label, true);
		}

		public Chart(String value, String label, boolean supportsFraction) {
			this.value = value;
			this.label = label;
			this.supportsFraction = supportsFraction;
		}

		public String fullUrl() {
			final String type = supportsFraction ? OPTION_TYPE.currentValue() : OPTION_TYPE_COUNT.value;
			return URL_BASE + OPTION_INSTANCE.currentValue() + URL_CHART + value + type
					+ OPTION_WINDOW.currentValue()
					+ URL_CUSTOMIZATION + URL_PARAM_TITLE + urlEncode(label) + URL_PARAM_HISTORY
					+ OPTION_HISTORY.currentValue() + "d";
		}

		public String getValue() {
			return value;
		}

		public String getLabel() {
			return label;
		}

		public boolean getSupportsFraction() {
			return supportsFraction;
		}
	}

	public static class ChartOption {
		private final String label;
		private final String name;
		private final ChartOptionValue defaultValue;
		private final List<ChartOptionValue> values;

		public ChartOption(String label, String name, ChartOptionValue defaultValue, ChartOptionValue... values) {
			this.label = label;
			this.name = name;
			this.defaultValue = defaultValue;
			this.values = Arrays.asList(values);
		}

		public String currentValue() {
			final String requestValue = request().getQueryString(name);
			for (ChartOptionValue cur : values)
				if (cur.getValue().equals(requestValue))
					return requestValue;

			return defaultValue.getValue();
		}

		public String getLabel() {
			return label;
		}

		public List<ChartOptionValue> getValues() {
			return values;
		}

		public ChartOptionValue getDefaultValue() {
			return defaultValue;
		}

		public String getName() {
			return name;
		}
	}

	public static class ChartOptionValue {
		private final String value;
		private final String label;

		public ChartOptionValue(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public String getLabel() {
			return label;
		}
	}

	public static class ChartGroup {
		private final String name;
		private final List<Chart> charts;

		public ChartGroup(String name, Chart... charts) {
			super();
			this.name = name;
			this.charts = Arrays.asList(charts);
		}

		public List<Chart> getCharts() {
			return charts;
		}

		public String getName() {
			return name;
		}

	}
}

