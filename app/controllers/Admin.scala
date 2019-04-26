package controllers

import java.io.File
import javax.inject.Inject
import models._
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import scala.concurrent.ExecutionContext
import scala.util.Try
import views.html.pages

class Admin @Inject()(implicit cfg: Configuration, db: Database) extends InjectedController {
	implicit val admin = true
	implicit val ec = ExecutionContext.global
	def root = Action(Ok(views.html.pages.lists(Sections.all)))
	def sums(id: Long) = Action(implicit req => Try(Ok(pages.proof(Record.forId(id).get))).getOrElse(NotFound(pages.index())))
	def logs(id: Long) = Action(Try(Ok.sendFile(content = new File(Record.forId(id).get.file))).getOrElse(NotFound(pages.index())))
	def post(id: Long) = Action(Ok(pages.index()))
}
