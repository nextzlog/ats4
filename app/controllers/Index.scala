package controllers

import javax.inject.Inject
import play.api.mvc.{Action, InjectedController}

import views._

class Index @Inject() extends InjectedController {
	def view = Action(Ok(html.index()))
}
