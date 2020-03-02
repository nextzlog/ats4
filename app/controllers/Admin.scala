package controllers

import java.nio.file.Paths
import javax.inject.{Inject,Singleton}
import models.{Major,Report,Submit,SubmitForm}
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
		Ok(entry(Try(SubmitForm.fill(Submit(call.get).get)).getOrElse(SubmitForm)))
	})
	def file(call: Option[String]) = Action(Ok.sendPath (
		Try(Paths.get(Major.ofCall(call.get).get.file)).getOrElse(Report.file),
		inline=false
	))
}
