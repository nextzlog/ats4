package controllers

import javax.inject.{Inject, Singleton}

import scala.util.Try

import play.api.Logger
import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{Person, Record, Report, SendMail}
import views.html.{pages => html}

@Singleton
class Proof @Inject()(implicit smtp: MailerClient) extends InjectedController {
	private implicit val admin = true
	def proof(test: String, call: String) = Action(implicit r => Ok(html.proof(test, call)))
	def table(test: String, call: String) = Action(Ok(Report.findAllByCall(call).head.data))
	def email(test: String, call: String) = Action(Ok(new SendMail().remind(call)))
	def clean(test: String, call: String) = Action(implicit r => Try {
		Person.findAllByCall(call).foreach(_.delete())
		Record.findAllByCall(call).foreach(_.delete())
		Report.findAllByCall(call).foreach(_.delete())
		Logger(getClass).info(s"deleted: $call")
		Ok(html.lists(test))
	}.getOrElse(NotFound(html.lists(test))))
}
