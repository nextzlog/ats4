package controllers

import play.api.mvc.{Action, InjectedController}
import javax.inject.Inject

import views._

class Quest @Inject() extends InjectedController {
	def quest = Action(Ok(html.quest()))
}
