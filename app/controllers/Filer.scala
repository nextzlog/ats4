package controllers

import javax.inject.Inject
import play.api.mvc.{Action, InjectedController}
import views.js

class Filer @Inject() extends InjectedController {
	def view(id: String) = Action(Ok(js.filer(id)))
}
