package controllers

import play.api.db.Database
import play.api.mvc.{Action, Controller}
import javax.inject._

import models.Post
import views._

class Board @Inject()(db: Database) extends Controller {
	def board = Action(Ok(html.board(Post.all(db))))
}
