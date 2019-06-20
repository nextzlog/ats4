package controllers

import javax.inject.{Inject,Singleton}
import play.api.Configuration
import play.api.db.Database
import play.api.http.HttpErrorHandler
import play.api.mvc.RequestHeader
import play.api.mvc.Results.{InternalServerError,Status}
import scala.concurrent.Future
import views.html.pages.index

@Singleton class Error extends HttpErrorHandler {
	@Inject implicit var cfg: Configuration = null
	@Inject implicit var db: Database = null
	override def onClientError(r: RequestHeader, s: Int, msg: String) = Future.successful(Status(s)(index(Some(s))))
	override def onServerError(r: RequestHeader, error: Throwable) = Future.successful(InternalServerError(index()))
}
