package controllers

import java.io.{File, IOException => UnsupException}
import java.util.{NoSuchElementException => OmissException}
import javax.inject.Inject
import models._
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import play.libs.mailer.MailerClient
import scala.concurrent.ExecutionContext
import scala.util.Try
import views.html.{pages, warns}

class Admin @Inject()(implicit smtp: MailerClient, cfg: Configuration, db: Database) extends InjectedController {
	implicit val admin = true
	implicit val ec = ExecutionContext.global
	def root = Action(Ok(views.html.pages.lists(Sections.all)))
	def sums(id: Long) = Action(implicit req => Try(Ok(pages.proof(Record.forId(id).get,None))).getOrElse(NotFound(pages.index())))
	def logs(id: Long) = Action(Try(Ok.sendFile(content = new File(Record.forId(id).get.file))).getOrElse(NotFound(pages.index())))
	def post(id: Long) = Action(parse.multipartFormData) (implicit req => Try {
		val file = req.body.file("eLog").get.ref
		Ok(pages.proof(new Acceptor().accept(Record.forId(id).get.scaned,file)))
	}.recover {
		case ex: UnsupException => {
			play.api.Logger.error(warns.unsup().body, ex)
			Ok(pages.proof(Record.forId(id).get, Some(warns.unsup())))
		}
	}.get)
}
