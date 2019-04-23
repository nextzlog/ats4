package controllers

import javax.inject.Inject
import models.{Post, Sect}
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import views.html.pages

class Proof @Inject()(cfg: Configuration)(implicit db: Database) extends InjectedController {
	implicit val admin = true
	def pull(id: Long) = Action(Ok(pages.proof(Post.forId(id).get)(cfg)))
	def push(id: Long) = Action(Ok(pages.proof(Post.forId(id).get)(cfg)))
}
