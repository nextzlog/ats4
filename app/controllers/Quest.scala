package controllers

import play.api.mvc.{Action, Controller}
import javax.inject._

import views._

class Quest @Inject() extends Controller {
	def quest = Action(Ok(html.quest()))
}
