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
import views.html.pages

class Entry @Inject() (implicit smtp: MailerClient, cfg: Configuration, db: Database) extends InjectedController {
	def form = Action(implicit req => Ok(if(Conf.isOK) pages.entry(User.form) else pages.index()))
	def post = Action(parse.multipartFormData) (implicit req => Try {
		val prof = User.form.bindFromRequest.get
		val elog = req.body.file("eLog").get.ref
		Ok(pages.proof(new Flow()(smtp,cfg,db)(prof,elog)))
	}.recover {
		case ex: OmissException => {
			play.api.Logger.error(Omiss.toString, ex)
			Ok(pages.entry(User.form.bindFromRequest,Some(Omiss)))
		}
		case ex: UnsupException => {
			play.api.Logger.error(Unsup.toString, ex)
			Ok(pages.entry(User.form.bindFromRequest,Some(Unsup)))
		}
	}.get)
}
