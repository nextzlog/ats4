package controllers

import javax.inject.{Inject,Singleton}
import models.Disposal
import models.Record.forId
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import scala.util.Try
import views.html.pages.{lists,proof}

@Singleton class Proof extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	def view(id: Long) = Action(implicit r=>{
		Ok(proof(forId(id).get))
	})
	def elim(id: Long) = Action(Try {
		Disposal(forId(id).get.scored)
		Ok(lists())
	}.getOrElse(NotFound(lists())))
}
