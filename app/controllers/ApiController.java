package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class ApiController extends Controller {

	public Result redirect() {
		return redirect(controllers.routes.ApiController.index());
	}

	public Result index() {
		return ok(views.html.api.render());
	}

}
