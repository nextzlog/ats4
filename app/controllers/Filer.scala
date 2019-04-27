package controllers

import javax.inject.Singleton
import play.api.mvc.{Action,InjectedController}

@Singleton class Filer extends InjectedController {
	def view(id: String) = Action(Ok(views.js.comps.upload(id)))
}
