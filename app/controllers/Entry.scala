package controllers

import java.io.{IOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import java.util.{NoSuchElementException => Omiss}
import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{Acceptor, PostForm, Schedule}
import views.html.pages.{entry, index, proof}
import views.html.warns.{chset, email, omiss, unsup}

import org.apache.commons.mail.{EmailException => Email}

@Singleton class Entry @Inject()(implicit smtp: MailerClient, cfg: Configuration) extends InjectedController {
	def form = Action(implicit r=>if(Schedule.isOK) Ok(entry(PostForm)) else Gone(index()))
	def post = Action(implicit r=> util.Try {
		val data = r.body.asMultipartFormData
		val form = PostForm.bindFromRequest().get
		val file1 = data.get.file("sheet1")
		val file2 = data.get.file("sheet2")
		val file3 = data.get.file("sheet3")
		val files = Seq(file1,file2,file3).flatten.map(_.ref)
		Ok(proof(new Acceptor().push(post=form,temps=files)))
	}.recover {
		case ex: Chset => Ok(entry(PostForm.bindFromRequest(), Some(chset())))
		case ex: Omiss => Ok(entry(PostForm.bindFromRequest(), Some(omiss())))
		case ex: Unsup => Ok(entry(PostForm.bindFromRequest(), Some(unsup())))
		case ex: Email => Ok(entry(PostForm.bindFromRequest(), Some(email())))
	}.get)
}
