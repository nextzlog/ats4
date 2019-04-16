package controllers

import javax.inject.Inject
import play.api.mvc.{Action, InjectedController}

import views._

class Quest @Inject() extends InjectedController {
	def view = Action(Ok(html.quest()))
}
