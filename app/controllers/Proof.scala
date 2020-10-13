package controllers

import javax.inject.{Inject,Singleton}
import models.{Person,Record}
import play.api.Logger
import play.api.mvc.{Action,InjectedController}
import scala.util.Try
import views.html.pages.{lists,proof}

@Singleton
class Proof extends InjectedController {
	private implicit val admin = true
	def view(call: String) = Action(implicit r=>Ok(proof(call)))
	def elim(call: String) = Action(Try {
		val persons = Person.findAllByCall(call)
		val records = Record.findAllByCall(call)
		persons.foreach(_.delete())
		records.foreach(_.delete())
		Logger(getClass).info(s"deleted: $records")
		Ok(lists())
	}.getOrElse(NotFound(lists())))
}
