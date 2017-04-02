package models

import play.api._
import javax.inject._
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global

class Bind extends play.api.inject.Module {
	def bindings(env: Environment, conf: Configuration) = Seq(bind[Init].toSelf.eagerly)
}
