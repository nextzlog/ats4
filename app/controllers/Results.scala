package controllers

import play.api._
import play.api.mvc._

import views._
import models._

object Results extends Controller {
	def results = Action(Ok(html.results(Section.all)))
}
