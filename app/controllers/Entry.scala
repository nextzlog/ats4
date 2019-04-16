package controllers

import java.io.{IOException => UnsupException}
import java.util.{NoSuchElementException => OmissException}
import javax.inject.Inject
import play.api.Logger
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import play.libs.mailer.MailerClient
import scala.util.Try

import views._
import models._

class Entry @Inject() (implicit smtp: MailerClient, db: Database) extends InjectedController {
	def submit = Action(implicit req => if(Conf.isOK) Ok(html.submit(Prof.form)) else BadRequest(html.index()))
	def accept = Action(parse.multipartFormData) (implicit req => {
		val prof = Prof.form.bindFromRequest
		val file = req.body.file("eLog")
		Try(Ok(html.accept(Scan(prof.get,file.get.ref)))).recover {
			case ex: OmissException => BadRequest(html.submit(prof,Some(Omiss)))
			case ex: UnsupException => BadRequest(html.submit(prof,Some(Unsup)))
		}.get
	})
}
