package controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.InjectedController

@Singleton class Index @Inject()(implicit cfg: Configuration) extends InjectedController {
	def view = Action(Ok(views.html.pages.index()))
}
