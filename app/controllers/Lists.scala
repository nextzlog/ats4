package controllers

import javax.inject.Inject
import models.Sect
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import views.html

class Lists @Inject()(cfg: Configuration, db: Database) extends InjectedController {
	def view = Action(Ok(html.lists(Sect.all(db))(cfg)))
}
