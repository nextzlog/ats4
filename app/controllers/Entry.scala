package controllers

import java.io.{IOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import java.util.{NoSuchElementException => Omiss}
import javax.inject.{Inject, Singleton}

import play.api.mvc.InjectedController
import play.libs.mailer.MailerClient

import models.{Acceptor, ClientForm}
import views.html.{pages, warns}

import org.apache.commons.mail.{EmailException => Email}

@Singleton class Entry @Inject()(implicit smtp: MailerClient) extends InjectedController {
	def entry(test: String) = Action(implicit r => util.Try {
		val data = r.body.asMultipartFormData
		val form = ClientForm(test).bindFromRequest()
		Ok(pages.proof(test, new Acceptor().push(post = form.get, files = data.get.files.map(_.ref))))
	}.recover {
		case ex: Chset => Ok(pages.entry(test, ClientForm(test).bindFromRequest(), Some(warns.chset())))
		case ex: Omiss => Ok(pages.entry(test, ClientForm(test).bindFromRequest(), Some(warns.omiss())))
		case ex: Unsup => Ok(pages.entry(test, ClientForm(test).bindFromRequest(), Some(warns.unsup())))
		case ex: Email => Ok(pages.entry(test, ClientForm(test).bindFromRequest(), Some(warns.email())))
	}.get)
}
