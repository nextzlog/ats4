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
	def form = Action(implicit req => Ok(if(DeadLine.isOK) pages.entry(UserForm) else pages.index()))
	def post = Action(parse.multipartFormData) (implicit req => Try {
		val prof = UserForm.bindFromRequest.get
		val elog = req.body.file("eLog").get.ref
		Ok(pages.proof(new WorkFlow()(smtp,cfg,db)(prof,elog)))
	}.recover {
		case ex: OmissException => {
			play.api.Logger.error(warns.omiss().body, ex)
			Ok(pages.entry(UserForm.bindFromRequest, Some(warns.omiss())))
		}
		case ex: UnsupException => {
			play.api.Logger.error(warns.unsup().body, ex)
			Ok(pages.entry(UserForm.bindFromRequest, Some(warns.unsup())))
		}
	}.get)
}
