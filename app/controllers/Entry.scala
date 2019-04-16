package controllers

import java.io.{File, IOException => UnsupException}
import java.util.{NoSuchElementException => OmissException}
import javax.inject.Inject
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import play.libs.mailer.MailerClient
import scala.util.Try

import views._
import models._

class Entry @Inject() (smtp: MailerClient, db: Database) extends InjectedController {
	def pull = Action(implicit req => Ok(if(Conf.isOK) html.entry(Prof.form) else html.index()))
	def push = Action(parse.multipartFormData) (implicit req => Try {
		val prof = Prof.form.bindFromRequest.get
		val elog = req.body.file("eLog").get.ref
		Ok(html.check(new Push()(smtp,db)(prof,elog)))
	}.recover {
		case ex: OmissException => {
			play.api.Logger.error(Omiss.toString, ex)
			BadRequest(html.entry(Prof.form.bindFromRequest,Some(Omiss)))
		}
		case ex: UnsupException => {
			play.api.Logger.error(Unsup.toString, ex)
			BadRequest(html.entry(Prof.form.bindFromRequest,Some(Unsup)))
		}
	}.get)
}
