package controllers

import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import javax.inject.Inject

import models.Post
import views._

class Board @Inject()(db: Database) extends InjectedController {
	def board = Action(Ok(html.board(Post.all(db))))
}
