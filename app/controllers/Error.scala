package controllers

import javax.inject.{Inject, Singleton}

import scala.concurrent.Future

import play.api.{Configuration, Logger}
import play.api.http.HttpErrorHandler
import play.api.mvc.RequestHeader
import play.api.mvc.Results.{InternalServerError, Status}

import views.html.pages.index

@Singleton class Error @Inject()(implicit cfg: Configuration) extends HttpErrorHandler {
	override def onClientError(r: RequestHeader, st: Int, msg: String) = {
		Future.successful(Status(st)(index(Some(st))))
	}
	override def onServerError(r: RequestHeader, throwable: Throwable) = {
		Logger(classOf[Error]).error("SERVER ERROR!", throwable)
		Future.successful(InternalServerError(index(Some(500))))
	}
}
