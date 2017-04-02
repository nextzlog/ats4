package controllers

import play.api.mvc.{Action, Controller}
import javax.inject._

import views._

class Index @Inject() extends Controller {
	def index = Action(Ok(html.index()))
}
