package controllers

import javax.inject.{Inject,Singleton}
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}

@Singleton class Index extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	def view = Action(Ok(views.html.pages.index()))
}
