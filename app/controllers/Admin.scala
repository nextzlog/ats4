package controllers

import javax.inject.Inject
import models.Sect
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import scala.util.Try
import views.html

class Admin @Inject()(cfg: Configuration, db: Database) extends InjectedController {
	def view = Action(Ok(html.admin(Sect.all(db))(cfg)))
}
