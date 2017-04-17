package controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Result;
import play.test.Helpers;

public class HomeControllerTest {

	private static final int HTTP_OK = 200;
	private static final String CONTENT_TYPE_HTML = "text/html";
	private static final String CHARSET_UTF8 = "utf-8";

	private HomeController subject;

	@Before
	public void before() {
		this.subject = new HomeController();
	}

	@Test
	public void index() {
		final Result result = Helpers.invokeWithContext(Helpers.fakeRequest(), () -> subject.index());
		assertNotNull(result);
		assertEquals(HTTP_OK, result.status());
		assertEquals(CONTENT_TYPE_HTML, result.contentType().get());
		assertEquals(CHARSET_UTF8, result.charset().get());
	}
}
