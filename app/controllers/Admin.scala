package controllers

import javax.inject.{Inject,Singleton}
import models.Format
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import scala.util.Try
import views.html.pages.{entry,lists}

@Singleton class Admin extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	def view = Action(Ok(lists()))
	def form(id: Option[Long]) = Action(implicit r=>{
		Ok(entry(Try(Format(id.get)).getOrElse(Format)))
	})
}
