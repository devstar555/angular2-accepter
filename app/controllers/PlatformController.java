package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class PlatformController extends Controller {
	public Result index() {
		return ok(views.html.home.render());
	}
}
