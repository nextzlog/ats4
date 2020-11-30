package controllers

import java.io.{IOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import java.util.{NoSuchElementException => Omiss}
import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{Acceptor, ClientForm, Schedule}
import views.html.pages.{entry, index, proof}
import views.html.warns.{chset, email, omiss, unsup}

import org.apache.commons.mail.{EmailException => Email}

@Singleton class Entry @Inject()(implicit smtp: MailerClient, cfg: Configuration) extends InjectedController {
	def form = Action(implicit r=>if(Schedule.openEntries) Ok(entry(ClientForm)) else Gone(index()))
	def post = Action(implicit r=> util.Try {
		val data = r.body.asMultipartFormData
		val form = ClientForm.bindFromRequest().get
		val file1 = data.get.file("sheet1")
		val file2 = data.get.file("sheet2")
		val file3 = data.get.file("sheet3")
		val files = Seq(file1,file2,file3).flatten.map(_.ref)
		Ok(proof(new Acceptor().push(post=form,files=files)))
	}.recover {
		case ex: Chset => Ok(entry(ClientForm.bindFromRequest(), Some(chset())))
		case ex: Omiss => Ok(entry(ClientForm.bindFromRequest(), Some(omiss())))
		case ex: Unsup => Ok(entry(ClientForm.bindFromRequest(), Some(unsup())))
		case ex: Email => Ok(entry(ClientForm.bindFromRequest(), Some(email())))
	}.get)
}
