package controllers

import javax.inject.Inject
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import scala.util.Try

import play.api.Logger

import views._
import models._

class Admin @Inject()(implicit db: Database) extends InjectedController {
	def view = Action(Ok(html.admin(Sect.all())))
}
