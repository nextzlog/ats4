package controllers

import javax.inject.{Inject,Singleton}
import play.api.mvc.{Action,InjectedController}

@Singleton class Guide extends InjectedController {
	def view = Action(Ok(views.html.pages.guide()))
}
