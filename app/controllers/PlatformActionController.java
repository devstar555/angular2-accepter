package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class PlatformActionController extends Controller {
	public Result index(String action) {
		return ok(views.html.home.render());
	}
}
