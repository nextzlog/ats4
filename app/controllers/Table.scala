package controllers

import play.api.mvc.{Action, InjectedController}
import javax.inject.Inject

import play.api.db.Database

import views._
import models._

class Table @Inject()(db: Database) extends InjectedController {
	def result = Action(Ok(html.result(Sect.all()(db))))
}
