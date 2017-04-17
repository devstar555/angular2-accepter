package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static play.mvc.Results.ok;

import java.io.IOException;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

import org.junit.Before;
import org.junit.Test;

import play.libs.ws.WS;
import play.libs.ws.WSClient;
import play.mvc.Result;
import play.routing.Router;
import play.routing.RoutingDsl;
import play.server.Server;
import play.test.Helpers;

public class ChartControllerTest {

	private static final int HTTP_OK = 200;
	private static final int HTTP_NOT_FOUND = 404;
	private static final String CONTENT_TYPE_HTML = "text/html";
	private static final String CHARSET_UTF8 = "utf-8";

	private ChartController subject;


	@Before
	public void before() {
		this.subject = new ChartController();



	}

	@Test
	public void groups() {
		assertNotEquals(0, subject.groups().size());
	}

	@Test
	public void options() {
		assertNotEquals(0, subject.options().size());
	}

	@Test
	public void simple() {
		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest(), () -> subject.index());
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_HTML, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
	}

	@Test
	public void bartizanTestSuccess() throws IOException, InterruptedException, ExecutionException {
		Router router = new RoutingDsl().GET("/rest/metric/graph").routeTo(() -> {
			return ok("Hello!");
		}).build();

		Server server = play.server.Server.forRouter(router);
		WSClient ws = WS.newClient(server.httpPort());
		subject.ws = ws;
		subject.baseUrl = "/rest/metric/graph";
		subject.timeout = "-1";

		final CompletionStage<Result> result = Helpers.invokeWithContext(Helpers.fakeRequest(),
				() -> subject.bartizan());

		Result r = result.toCompletableFuture().get();

		assertEquals(HTTP_OK, r.status());
		assertEquals(CHARSET_UTF8, r.charset().get());

		ws.close();
		server.stop();
	}

	@Test
	public void bartizanTestError() throws IOException, InterruptedException, ExecutionException {
		Router router = new RoutingDsl().GET("/rest/metric/graph").routeTo(() -> {
			return ok("Hello!");
		}).build();

		Server server = play.server.Server.forRouter(router);
		WSClient ws = WS.newClient(server.httpPort());
		subject.ws = ws;
		subject.baseUrl = "/wrong_url";
		subject.timeout = "-1";

		final CompletionStage<Result> result = Helpers.invokeWithContext(Helpers.fakeRequest(),
				() -> subject.bartizan());

		Result r = result.toCompletableFuture().get();

		assertEquals(HTTP_NOT_FOUND, r.status());

		ws.close();
		server.stop();
	}
}
