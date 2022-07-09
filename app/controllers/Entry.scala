package controllers

import java.util.NoSuchElementException
import javax.inject.{Inject, Singleton}

import play.api.mvc.InjectedController
import play.filters.csrf.CSRF
import play.libs.mailer.MailerClient

import models.{Acceptor, ContestForm}
import views.html.{pages, warns}

@Singleton class Entry @Inject()(implicit smtp: MailerClient) extends InjectedController {
	def entry = Action(implicit r => if(CSRF.getToken.isDefined) util.Try {
		val data = r.body.asMultipartFormData
		val form = ContestForm.bindFromRequest()
		Ok(pages.proof(new Acceptor().push(post = form.get, files = data.get.files.map(_.ref))))
	}.recover {
		case ex: NoSuchElementException => Ok(pages.entry(ContestForm.bindFromRequest(), Some(warns.omiss())))
	}.get else Ok(warns.token()))
}
