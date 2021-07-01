package controllers

import java.io.{UncheckedIOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import javax.inject.{Inject, Singleton}

import play.api.mvc.InjectedController

import models.Verifier
import views.html.{pages, warns}

@Singleton class Trial @Inject() extends InjectedController {
	val valid = new Verifier
	def trial = this.check(admin = false)
	def admin = this.check(admin = true)
	def check(implicit admin: Boolean) = Action(implicit r => util.Try {
		valid.push(files = r.body.asMultipartFormData.get.files.map(_.ref))
		Ok(pages.trial(succ = true))
	}.recover {
		case ex: Chset => Ok(pages.trial(warn = Some(warns.chset()), ex=Some(ex)))
		case ex: Unsup => Ok(pages.trial(warn = Some(warns.unsup()), ex=Some(ex)))
	}.get)
}
