package controllers

import java.io.{UncheckedIOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import java.util.{NoSuchElementException => Omiss}
import javax.inject.{Inject, Singleton}

import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{Acceptor, ClientForm}
import views.html.{pages, warns}

import org.apache.commons.mail.{EmailException => Email}

@Singleton class Entry @Inject()(implicit smtp: MailerClient) extends InjectedController {
	def entry = Action(implicit r => util.Try {
		val data = r.body.asMultipartFormData
		val form = ClientForm.bindFromRequest()
		Ok(pages.proof(new Acceptor().push(post = form.get, files = data.get.files.map(_.ref))))
	}.recover {
		case ex: Chset => Ok(pages.entry(ClientForm.bindFromRequest(), Some(warns.chset())))
		case ex: Omiss => Ok(pages.entry(ClientForm.bindFromRequest(), Some(warns.omiss())))
		case ex: Unsup => Ok(pages.entry(ClientForm.bindFromRequest(), Some(warns.unsup())))
		case ex: Email => Ok(pages.entry(ClientForm.bindFromRequest(), Some(warns.email())))
	}.get)
}
