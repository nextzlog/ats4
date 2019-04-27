package controllers

import java.io.{File,IOException=>Unsup}
import java.util.{NoSuchElementException=>Omiss}
import javax.inject.{Inject,Singleton}
import models.Record.forId
import models.{Acceptor,Sections}
import util.Try
import play.api.Configuration
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import play.libs.mailer.MailerClient
import scala.concurrent.ExecutionContext
import views.html.pages.{index,lists,proof}
import views.html.warns.{omiss,unsup}

@Singleton class Admin extends InjectedController {
	@Inject implicit var smtp: MailerClient = null
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	private implicit val ec = ExecutionContext.global
	def root = Action(Ok(lists(Sections.all)))
	def sums(id: Long) = Action(implicit r=>Try {
		Ok(proof(forId(id).get))
	}.getOrElse {
		NotFound(lists(Sections.all))
	})
	def logs(id: Long) = Action(Try {
		Ok.sendFile(content=new File(forId(id).get.file))
	}.getOrElse {
		NotFound(lists(Sections.all))
	})
	def post(id: Long) = Action(implicit r=>util.Try {
		val data = r.body.asMultipartFormData
		val file = data.get.file("sheet").get
		val scan = forId(id).get.scaned
		Ok(proof(new Acceptor().accept(scan,file.ref)))
	}.recover {
		case ex: Omiss => Ok(proof(forId(id).get,Some(omiss())))
		case ex: Unsup => Ok(proof(forId(id).get,Some(unsup())))
	}.get)
}
