package controllers

import java.io.File
import javax.inject.{Inject,Singleton}
import models.Record.forId
import models.{Format,Sections}
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import play.api.{Configuration,Logger}
import scala.concurrent.ExecutionContext
import views.html.pages.{entry,lists,proof}

@Singleton class Admin extends InjectedController {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	private implicit val admin = true
	private implicit val ec = ExecutionContext.global
	def root = Action(Ok(lists(Sections.all)))
	def form(id: Long) = Action(implicit r=>util.Try {
		val scaned = forId(id).get.scaned.copy(addr=null)
		Ok(entry(Format.fill(scaned)))
	}.getOrElse {
		Ok(entry(Format))
	})
	def logs(id: Long) = Action(util.Try {
		Ok.sendFile(new File(forId(id).get.file),inline=false)
	}.getOrElse {
		NotFound(lists(Sections.all))
	})
	def sums(id: Long) = Action(implicit r=>util.Try {
		Ok(proof(forId(id).get))
	}.getOrElse {
		NotFound(lists(Sections.all))
	})
	def elim(id: Long) = Action(implicit r=>util.Try {
		val logger = Logger(classOf[Admin])
		logger.info(s"deleted: ${forId(id)}")
		forId(id).get.purge
		Ok(lists(Sections.all))
	}.getOrElse {
		NotFound(lists(Sections.all))
	})
}
