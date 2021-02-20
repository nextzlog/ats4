package controllers

import javax.inject.{Inject, Singleton}

import scala.concurrent.ExecutionContext

import play.api.mvc.InjectedController

import models.{Client, ClientForm, Person, Record, Report}
import views.html.{pages => html}
import views.txt.{pages => text}

@Singleton class Admin @Inject()(implicit ec: ExecutionContext) extends InjectedController {
	private implicit val admin = true
	def index(test: String) = Action(implicit r => Ok(html.lists(test)))
	def excel(test: String) = Action(Ok(text.excel(test).body.trim))
	def entry(test: String) = Action(implicit r => Ok(html.entry(test, ClientForm(test))))
	def force(test: String, call: String) = Action(implicit r => Ok(html.entry(test, ClientForm(test).fill(Client.fill(call)))))
	def clean(test: String) = Action(implicit r => {
		Person.forceDeleteAll()
		Record.forceDeleteAll()
		Report.forceDeleteAll()
		Ok(html.lists(test))
	})
}
