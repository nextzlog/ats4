package controllers

import javax.inject.Inject
import models.{Post, Sect}
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import scala.util.Try
import views.html

class Posts @Inject()(cfg: Configuration)(implicit db: Database) extends InjectedController {
	def pull(id: Long) = Action(Try(Ok(html.posts(Post.forId(id).get)(cfg))).getOrElse(BadRequest(html.admin(Sect.all)(cfg))))
	def push(id: Long) = Action(Try(Ok(html.posts(Post.forId(id).get)(cfg))).getOrElse(BadRequest(html.admin(Sect.all)(cfg))))
}
