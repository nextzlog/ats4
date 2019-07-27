package controllers

import javax.inject.{Inject,Singleton}
import models.Record.forId
import models.{Format,Report}
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import scala.concurrent.ExecutionContext
import scala.util.Try
import views.html.pages.{entry,lists}

@Singleton class Admin extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	private implicit val ec = ExecutionContext.global
	def view = Action(Ok(lists()))
	def edit(id: Option[Long]) = Action(implicit r=>{
		Ok(entry(Try(Format(id.get)).getOrElse(Format)))
	})
	def file(id: Option[Long]) = Action(Ok.sendPath (
		Try(forId(id.get).get.path).getOrElse(Report.file),
		inline=false
	))
}
