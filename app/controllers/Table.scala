package controllers

import javax.inject.Inject
import models.{Elog, Post}
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import scala.concurrent.ExecutionContext

class Table @Inject()(implicit db: Database) extends InjectedController {
	implicit val ec = ExecutionContext.global
	def pull(id: Long) = Action(Ok.sendFile(content = Elog(Post.forId(id).get.elog)))
}
