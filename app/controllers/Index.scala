package controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.InjectedController

import models.{ClientForm, Schedule}
import views.html.{pages => html}

@Singleton class Index @Inject()(implicit cfg: Configuration) extends InjectedController {
	def calls(test: String) = Action(Ok(html.calls(test)))
	def forum(test: String) = Action(Ok(html.forum(test)))
	def guide(test: String) = Action(Ok(html.guide(test)))
	def lists(test: String) = Action(implicit r => Ok(html.lists(test)))
	def entry(test: String) = if (Schedule.accept) Action(implicit r => Ok(html.entry(test, ClientForm(test)))) else index(test)
	def index(test: String) = Action(Ok(html.index(test)))
}
