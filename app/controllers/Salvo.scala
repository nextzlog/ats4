package controllers

import javax.inject.{Inject,Singleton}
import models.{Book,SendMail}
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import play.libs.mailer.MailerClient
import views.html.pages.salvo

@Singleton
class Salvo extends InjectedController {
	@Inject implicit var smtp: MailerClient = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	def view = Action(implicit r=>Ok(salvo(done=false)))
	def send = Action(implicit r=> {
		new SendMail().sendAll(Book.all)
		Ok(salvo(done=true))
	})
}
