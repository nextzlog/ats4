package controllers

import play.api.mvc.{Action, Controller}
import javax.inject._

import play.api.db.Database

import views._
import models._

class Result @Inject()(db: Database) extends Controller {
	def result = Action(Ok(html.result(Sect.all()(db))))
}
