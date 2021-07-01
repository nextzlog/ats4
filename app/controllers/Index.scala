package controllers

import javax.inject.{Inject, Singleton}

import play.api.Configuration
import play.api.mvc.InjectedController

import models.{ContestForm, Schedule}
import views.html.{pages => html}

@Singleton class Index @Inject()(implicit cfg: Configuration) extends InjectedController {
	def calls = Action(Ok(html.calls()))
	def forum = Action(Ok(html.forum()))
	def guide = Action(Ok(html.guide()))
	def lists = Action(implicit r => Ok(html.lists()))
	def trial = Action(implicit e => Ok(html.trial()))
	def entry = if (Schedule.accept) Action(implicit r => Ok(html.entry(ContestForm))) else index
	def index = Action(Ok(html.index()))
}
