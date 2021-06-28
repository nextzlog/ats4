package controllers

import java.io.{UncheckedIOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import javax.inject.{Inject, Singleton}

import play.api.mvc.InjectedController

import models.Verifier
import views.html.{pages, warns}

@Singleton class Trial @Inject() extends InjectedController {
	val valid = new Verifier
	def trial = Action(implicit r => util.Try {
		val data = r.body.asMultipartFormData
		valid.push(files = data.get.files.map(_.ref))
		Ok(pages.trial(succ = true))
	}.recover {
		case ex: Chset => Ok(pages.trial(warn = Some(warns.chset())))
		case ex: Unsup => Ok(pages.trial(warn = Some(warns.unsup())))
	}.get)
}
