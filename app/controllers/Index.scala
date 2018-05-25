package controllers

import play.api.mvc.{Action, InjectedController}
import javax.inject.Inject

import views._

class Index @Inject() extends InjectedController {
	def index = Action(Ok(html.index()))
}
