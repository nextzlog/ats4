package controllers

import javax.inject.Inject
import models.Post
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import views.html

class Board @Inject()(cfg: Configuration, db: Database) extends InjectedController {
	def view = Action(Ok(html.board(Post.all(db))(cfg)))
}
