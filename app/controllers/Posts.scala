package controllers

import javax.inject.Inject
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import scala.util.Try

import play.api.Logger

import views._
import models._

class Posts @Inject()(implicit db: Database) extends InjectedController {
	def pull(id: Long) = Action(Try(Ok(html.posts(Post.forId(id).get))).getOrElse(BadRequest(html.admin(Sect.all))))
	def push(id: Long) = Action(Try(Ok(html.posts(Post.forId(id).get))).getOrElse(BadRequest(html.admin(Sect.all))))
}
