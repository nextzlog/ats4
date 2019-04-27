package controllers

import javax.inject.{Inject,Singleton}
import play.api.Configuration
import play.api.mvc.{Action,InjectedController}

@Singleton class Index extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	def view = Action(Ok(views.html.pages.index()))
}
