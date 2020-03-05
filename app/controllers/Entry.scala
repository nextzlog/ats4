package controllers

import java.io.{IOException=>Unsup}
import java.util.{NoSuchElementException=>Omiss}
import javax.inject.{Inject,Singleton}
import models.{Schedule,Scoring,Post,PostForm}
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
	def form = Action(implicit r=>if(Schedule.isOK) Ok(entry(PostForm)) else Gone(index()))
	def post = Action(implicit r=> util.Try {
		val data = r.body.asMultipartFormData
		val form = PostForm.bindFromRequest.get
		val file = data.get.file("sheet").get
		Ok(proof(new Scoring().push(form,file.ref)))
	}.recover {
		case ex: Omiss => Ok(entry(PostForm.bindFromRequest, Some(omiss())))
		case ex: Unsup => Ok(entry(PostForm.bindFromRequest, Some(unsup())))
	}.get)
}
