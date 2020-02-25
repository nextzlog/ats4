package controllers

import java.io.{IOException=>Unsup}
import java.util.{NoSuchElementException=>Omiss}
import javax.inject.{Inject,Singleton}
import models.{Binds,Schedule,Submit}
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import play.libs.mailer.MailerClient
import views.html.pages.{entry,index,proof}
import views.html.warns.{omiss,unsup}

@Singleton class Entry extends InjectedController {
	@Inject implicit var smtp: MailerClient = null
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	def form = Action(implicit r=>if(Schedule.isOK) Ok(entry(Binds)) else Gone(index()))
	def post = Action(implicit r=> util.Try {
		val data = r.body.asMultipartFormData
		val form = Binds.bindFromRequest.get
		val file = data.get.file("sheet").get
		Ok(proof(form.push(file.ref)))
	}.recover {
		case ex: Omiss => Ok(entry(Binds.bindFromRequest, Some(omiss())))
		case ex: Unsup => Ok(entry(Binds.bindFromRequest, Some(unsup())))
	}.get)
}
