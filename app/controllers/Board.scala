package controllers

import javax.inject.Inject
import models.Post
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}

class Board @Inject()(cfg: Configuration, db: Database) extends InjectedController {
	def view = Action(Ok(views.html.pages.board(Post.all(db))(cfg)))
}
