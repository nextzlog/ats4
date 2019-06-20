package controllers

import javax.inject.{Inject,Singleton}
import models.Record.forId
import models.Report
import play.api.db.Database
import play.api.mvc.{Action,InjectedController}
import scala.concurrent.ExecutionContext
import scala.util.Try

@Singleton class Table extends InjectedController {
	@Inject implicit var db: Database = null
	private implicit val admin = true
	private implicit val ec = ExecutionContext.global
	def file(id: Option[Long]) = Action(Ok.sendPath (
		Try(forId(id.get).get.path).getOrElse(Report.file),
		inline=false
	))
}
