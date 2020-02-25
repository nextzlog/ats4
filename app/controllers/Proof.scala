package controllers

import javax.inject.{Inject,Singleton}
import models.{Major,Minor}
import play.api.{Configuration,Logger}
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import scala.util.Try
import views.html.pages.{lists,proof}

@Singleton class Proof extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	def view(call: String) = Action(implicit r=>{
		Ok(proof(Major.ofCall(call).get))
	})
	def elim(call: String) = Action(Try {
		val major = Major.ofCall(call)
		val minor = Minor.ofCall(call)
		major.foreach(_.elim)
		minor.foreach(_.elim)
		Logger(getClass).info(s"deleted: $major")
		Ok(lists())
	}.getOrElse(NotFound(lists())))
}
