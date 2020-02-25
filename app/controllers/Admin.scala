package controllers

import java.nio.file.Paths
import javax.inject.{Inject,Singleton}
import models.{Binds,Major,Report,Submit}
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
	def edit(call: Option[String]) = Action(implicit r=>{
		Ok(entry(Try(Binds.fill(Submit(call.get).get)).getOrElse(Binds)))
	})
	def file(call: Option[String]) = Action(Ok.sendPath (
		Try(Paths.get(Major.ofCall(call.get).get.file)).getOrElse(Report.file),
		inline=false
	))
}
