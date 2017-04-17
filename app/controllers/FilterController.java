package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class FilterController extends Controller {

	public Result index(String name) {
		return ok(views.html.filters.render());
	}
}
