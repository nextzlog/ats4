package controllers

import play.api.mvc.{Action, InjectedController}
import javax.inject.Inject

import views._

class Filer @Inject() extends InjectedController {
	def upload(id: String) = Action(Ok(js.upload(id)))
}
