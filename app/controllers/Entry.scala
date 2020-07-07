package controllers

import java.io.{IOException=>Unsup}
import java.nio.charset.{CharacterCodingException=>Chset}
import java.util.{NoSuchElementException=>Omiss}
import javax.inject.{Inject,Singleton}
import models.{Schedule,Scoring,PostForm}
import org.apache.commons.mail.{EmailException=>Email}
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import play.libs.mailer.MailerClient
import views.html.pages.{entry,index,proof}
import views.html.warns.{chset,email,omiss,unsup}

@Singleton class Entry extends InjectedController {
	@Inject implicit var smtp: MailerClient = null
	@Inject implicit var db: Database = null
	def form = Action(implicit r=>if(Schedule.isOK) Ok(entry(PostForm)) else Gone(index()))
	def post = Action(implicit r=> util.Try {
		val data = r.body.asMultipartFormData
		val form = PostForm.bindFromRequest.get
		val file = data.get.file("sheet1").get
		Ok(proof(new Scoring().push(form,file.ref)))
	}.recover {
		case ex: Chset => Ok(entry(PostForm.bindFromRequest, Some(chset())))
		case ex: Omiss => Ok(entry(PostForm.bindFromRequest, Some(omiss())))
		case ex: Unsup => Ok(entry(PostForm.bindFromRequest, Some(unsup())))
		case ex: Email => Ok(entry(PostForm.bindFromRequest, Some(email())))
	}.get)
}
