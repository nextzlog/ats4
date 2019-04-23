package controllers

import javax.inject.Inject
import models.Sect
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}

class Admin @Inject()(cfg: Configuration, db: Database) extends InjectedController {
	implicit val admin = true
	def view = Action(Ok(views.html.pages.lists(Sect.all(db))(cfg)))
}
