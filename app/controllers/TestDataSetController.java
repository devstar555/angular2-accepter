package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class TestDataSetController extends Controller {
	public Result index() {
		return ok(views.html.reason.render());
	}
}
