package controllers

import javax.inject.Inject
import play.api.Configuration
import play.api.mvc.{Action, InjectedController}
import views.html

class Quest @Inject()(cfg: Configuration) extends InjectedController {
	def view = Action(Ok(html.quest(cfg)))
}
