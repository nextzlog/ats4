package controllers

import play.api._
import play.api.mvc._

import views._

object Index extends Controller {
	def index = Action(Ok(html.index()))
}
