package controllers

import java.nio.file.Paths
import javax.inject.{Inject,Singleton}
import models.{Call,Team,Post,PostForm,Storage}
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import scala.concurrent.ExecutionContext
import scala.util.Try
import views.txt.pages.excel
import views.html.pages.{entry,lists}

@Singleton class Admin extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	private implicit val ec = ExecutionContext.global
	def view = Action(Ok(lists()))
	def data = Action(Ok(excel().body.trim))
	def edit(call: String) = Action(implicit r=> Ok(entry(Call(call).post)))
	def file(call: String) = Action(Ok.sendPath(Paths.get(Call(call).game.head.file)))
}
