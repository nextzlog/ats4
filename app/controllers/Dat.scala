package controllers

import play.api._
import play.api.mvc._

import models._

object Dat extends Controller {
	def dat = Action(Ok.sendFile(content = DatFile.file, fileName = _ => DatFile.name))
}
