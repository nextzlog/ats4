package controllers

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

import play.api.mvc.InjectedController

import models.{ContestData, ContestForm, StationData, RankingData, LogBookData}
import views.html.{pages => html}
import views.txt.{pages => text}

@Singleton class Admin @Inject()(implicit ec: ExecutionContext) extends InjectedController {
	private implicit val admin = true
	def index = Action(implicit r => Ok(html.lists()))
	def excel = Action(Ok(text.excel().body.trim))
	def trial = Action(implicit r => Ok(html.trial()))
	def entry = Action(implicit r => Ok(html.entry(ContestForm)))
	def force(call: String) = Action(implicit r => Ok(html.entry(ContestForm.fill(ContestData.fill(call)))))
	def clean = Action(implicit r => {
		StationData.forceDeleteAll()
		RankingData.forceDeleteAll()
		LogBookData.forceDeleteAll()
		Ok(html.lists())
	})
}
