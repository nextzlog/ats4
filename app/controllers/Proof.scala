package controllers

import javax.inject.{Inject,Singleton}
import models.{Book,Call,Team,Game}
import play.api.{Configuration,Logger}
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import scala.util.Try
import views.html.pages.{lists,proof}

@Singleton
class Proof extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	def view(call: String) = Action(implicit r=>Ok(proof(Call(call).team.head)))
	def elim(call: String) = Action(Try {
		val team = Call(call).team
		val game = Call(call).game
		team.foreach(Book.del)
		game.foreach(Book.del)
		Logger(getClass).info(s"deleted: $team")
		Ok(lists())
	}.getOrElse(NotFound(lists())))
}
