package controllers

import javax.inject.Singleton

import play.api.mvc.InjectedController

@Singleton class Forum extends InjectedController {
	def view = Action(Ok(views.html.pages.forum()))
}
