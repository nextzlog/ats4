package controllers

import play.api.mvc.{Action,Controller}

class Filer extends Controller {
	def view(id: String) = Action(Ok(views.js.comps.upload(id)))
}
