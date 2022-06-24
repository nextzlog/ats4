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
		r.body.asMultipartFormData.get.files.map(_.ref).foreach(valid.test)
		Ok(warns.ready())
	}.recover {
		case ex: Chset => Ok(warns.chset())
		case ex: Unsup => Ok(warns.unsup())
	}.get)
}
