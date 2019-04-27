package controllers

import javax.inject.{Inject,Singleton}
import play.api.Configuration
import play.api.mvc.{Action,InjectedController}

@Singleton class Guide extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	def view = Action(Ok(views.html.pages.guide()))
}
