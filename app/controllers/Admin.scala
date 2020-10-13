package controllers

import java.nio.file.Paths
import javax.inject.{Inject,Singleton}
import models.{Chrono,Post,SendMail}
import play.api.mvc.{Action,InjectedController}
import play.libs.mailer.MailerClient
import scala.concurrent.ExecutionContext
import views.html.pages.{entry,lists}
import views.txt.pages.excel

@Singleton class Admin @Inject()(implicit smtp: MailerClient, ec: ExecutionContext) extends InjectedController {
	private implicit val admin = true
	def view = Action(Ok(lists()))
	def data = Action(Ok(excel().body.trim))
	def edit(call: String) = Action(implicit r=> Ok(entry(Post.form(call))))
	def file(call: String) = Action(Ok(Chrono.findAllByCall(call).head.data))
	def send(call: String) = Action(implicit r=> Ok(new SendMail().remind(call)))
}
