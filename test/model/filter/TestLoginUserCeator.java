package model.filter;

import java.util.Base64;

import play.mvc.Http;
import play.mvc.Http.RequestBuilder;

public class TestLoginUserCeator {
	public static void createHttpBasicAuth() {
		RequestBuilder builder = new RequestBuilder();
		String authData = "user" + ":" + "pass";
		String authDataEnc = Base64.getEncoder().encodeToString(authData.getBytes());
		builder.headers().put("Authorization", new String[] { "Basic " + authDataEnc });
		Http.Context context = new Http.Context(builder);
		Http.Context.current.set(context);
	}
}
