package controllers

import javax.inject.Inject
import play.api.mvc.{Action, InjectedController}

class Filer @Inject() extends InjectedController {
	def view(id: String) = Action(Ok(views.js.comps.upload(id)))
}
