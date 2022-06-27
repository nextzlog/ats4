package controllers

import java.util.{NoSuchElementException => OmissException}
import javax.inject.{Inject, Singleton}

import play.api.mvc.InjectedController
import play.filters.csrf.CSRF
import play.libs.mailer.MailerClient

import models.{Acceptor, ContestForm}
import views.html.{pages, warns}

import org.apache.commons.mail.EmailException

@Singleton class Entry @Inject()(implicit smtp: MailerClient) extends InjectedController {
	def entry = Action(implicit r => if(CSRF.getToken.isDefined) util.Try {
		val data = r.body.asMultipartFormData
		val form = ContestForm.bindFromRequest()
		Ok(pages.proof(new Acceptor().push(post = form.get, files = data.get.files.map(_.ref))))
	}.recover {
		case ex: OmissException => Ok(pages.entry(ContestForm.bindFromRequest(), Some(warns.omiss())))
		case ex: EmailException => Ok(pages.entry(ContestForm.bindFromRequest(), Some(warns.email())))
	}.get else Ok(pages.entry(ContestForm, Some(warns.token()))))
}
