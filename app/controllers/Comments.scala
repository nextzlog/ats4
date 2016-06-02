package controllers

import play.api._
import play.api.mvc._

import views._
import models._

object Comments extends Controller {
	def comments = Action(Ok(html.comments(Storage.enabled)))
}
