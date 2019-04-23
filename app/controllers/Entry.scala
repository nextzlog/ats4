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

class Entry @Inject() (smtp: MailerClient, cfg: Configuration, db: Database) extends InjectedController {
	def pull = Action(implicit req => Ok(if(Conf.isOK) pages.entry(Prof.form)(cfg) else pages.index(cfg)))
	def push = Action(parse.multipartFormData) (implicit req => Try {
		val prof = Prof.form.bindFromRequest.get
		val elog = req.body.file("eLog").get.ref
		Ok(pages.proof(new Flow()(smtp,cfg,db)(prof,elog))(cfg))
	}.recover {
		case ex: OmissException => {
			play.api.Logger.error(Omiss.toString, ex)
			BadRequest(pages.entry(Prof.form.bindFromRequest,Some(Omiss))(cfg))
		}
		case ex: UnsupException => {
			play.api.Logger.error(Unsup.toString, ex)
			BadRequest(pages.entry(Prof.form.bindFromRequest,Some(Unsup))(cfg))
		}
	}.get)
}
