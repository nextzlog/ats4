package controllers

import java.util.{NoSuchElementException => Omiss}
import javax.inject.{Inject, Singleton}

import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{Acceptor, ContestForm}
import views.html.{pages, warns}

import org.apache.commons.mail.{EmailException => Email}

@Singleton class Entry @Inject()(implicit smtp: MailerClient) extends InjectedController {
	def entry = Action(implicit r => util.Try {
		val data = r.body.asMultipartFormData
		val form = ContestForm.bindFromRequest()
		Ok(pages.proof(new Acceptor().push(post = form.get, files = data.get.files.map(_.ref))))
	}.recover {
		case ex: Omiss => Ok(pages.entry(ContestForm.bindFromRequest(), Some(warns.omiss())))
		case ex: Email => Ok(pages.entry(ContestForm.bindFromRequest(), Some(warns.email())))
	}.get)
}
