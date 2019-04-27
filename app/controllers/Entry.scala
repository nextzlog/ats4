package controllers

import java.io.{File, IOException => UnsupException}
import java.util.{NoSuchElementException => OmissException}
import javax.inject.Inject
import models._
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import play.libs.mailer.MailerClient
import scala.util.Try
import views.html.{pages, warns}

class Entry @Inject() (implicit smtp: MailerClient, cfg: Configuration, db: Database) extends InjectedController {
	def form = Action(implicit req => Ok(if(DeadLine.isOK) pages.entry(Scaner) else pages.index()))
	def post = Action(parse.multipartFormData) (implicit req => Try {
		val prof = Scaner.bindFromRequest.get
		val file = req.body.file("eLog").get.ref
		Ok(pages.proof(new Acceptor().accept(prof,file),None))
	}.recover {
		case ex: OmissException => {
			play.api.Logger.error(warns.omiss().body, ex)
			Ok(pages.entry(Scaner.bindFromRequest, Some(warns.omiss())))
		}
		case ex: UnsupException => {
			play.api.Logger.error(warns.unsup().body, ex)
			Ok(pages.entry(Scaner.bindFromRequest, Some(warns.unsup())))
		}
	}.get)
}
