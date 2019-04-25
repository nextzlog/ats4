package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}

class Lists @Inject()(implicit cfg: Configuration, db: Database) extends InjectedController {
	def view = Action(Ok(views.html.pages.lists(models.Test.sects)))
}
