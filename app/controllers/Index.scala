package controllers

import javax.inject.{Inject,Singleton}
import play.api.Configuration
import play.api.mvc.{Action,InjectedController}

@Singleton class Index @Inject()(implicit cfg: Configuration) extends InjectedController {
	def view = Action(Ok(views.html.pages.index()))
}
