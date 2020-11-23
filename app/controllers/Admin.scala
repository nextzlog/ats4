package controllers

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{Client, Report, SendMail}
import views.html.pages.{entry, lists}
import views.txt.pages.excel

@Singleton class Admin @Inject()(implicit smtp: MailerClient, ec: ExecutionContext) extends InjectedController {
	private implicit val admin = true
	def view = Action(Ok(lists()))
	def data = Action(Ok(excel().body.trim))
	def edit(call: String) = Action(implicit r=> Ok(entry(Client.form(call))))
	def file(call: String) = Action(Ok(Report.findAllByCall(call).head.data))
	def send(call: String) = Action(implicit r=> Ok(new SendMail().remind(call)))
}
