package controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.InjectedController

import models.{ClientForm, Schedule}
import views.html.{pages => html}

@Singleton class Index @Inject()(implicit cfg: Configuration) extends InjectedController {
	def index = Action(Ok(html.index()))
	def calls = Action(Ok(html.calls()))
	def forum = Action(Ok(html.forum()))
	def guide = Action(Ok(html.guide()))
	def lists = Action(implicit r=>Ok(html.lists()))
	def entry = if(Schedule.openEntries) Action(implicit r=>Ok(html.entry(ClientForm))) else index
}
