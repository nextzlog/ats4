package controllers

import play.api._
import play.api.mvc._

import views._
import models._

object Faq extends Controller {
	def faq = Action(Ok(html.faq()))
}
