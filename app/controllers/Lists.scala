package controllers

import javax.inject.Inject
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}

import views._
import models._

class Lists @Inject()(implicit db: Database) extends InjectedController {
	def view = Action(Ok(html.lists(Sect.all())))
}
