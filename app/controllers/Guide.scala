package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.mvc.{Action, InjectedController}

class Guide @Inject()(implicit cfg: Configuration) extends InjectedController {
	def view = Action(Ok(views.html.pages.guide()))
}
