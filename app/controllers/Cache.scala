package controllers

import javax.inject.{Inject, Singleton}

import scala.concurrent.{ExecutionContext, Future}

import play.api.Configuration
import play.api.mvc.{Filter, RequestHeader, Result}

import akka.stream.Materializer

@Singleton class Cache @Inject()(implicit val mat: Materializer, ec: ExecutionContext, cfg: Configuration) extends Filter {
	def apply(next: RequestHeader => Future[Result])(header: RequestHeader) = next(header).map((r: Result) => {
		cfg.getOptional[String]("cache.control").map(c => r.withHeaders("Cache-Control" -> c)).getOrElse(r)
	})
}
