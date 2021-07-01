package controllers

import javax.inject.{Inject, Singleton}

import scala.util.Try

import play.api.Logger
import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{StationData, RankingData, LogBookData, RawBookData, SendMail}
import views.html.{pages => html}

@Singleton
class Proof @Inject()(implicit smtp: MailerClient) extends InjectedController {
	private implicit val admin = true
	def proof(call: String) = Action(implicit r => Ok(html.proof(call)))
	def table(id: Long) = Action(Ok(LogBookData.findByID(id).head.data))
	def sheet(id: Long) = Action(Ok(RawBookData.findByID(id).head.data))
	def email(call: String) = Action(Ok(new SendMail().remind(call)))
	def clean(call: String) = Action(implicit r => Try {
		StationData.findAllByCall(call).foreach(_.delete())
		RankingData.findAllByCall(call).foreach(_.delete())
		LogBookData.findAllByCall(call).foreach(_.delete())
		Logger(getClass).info(s"deleted: $call")
		Ok(html.lists())
	}.getOrElse(NotFound(html.lists())))
}
