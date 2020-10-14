package controllers

import javax.inject.Singleton

import play.api.mvc.InjectedController

@Singleton class Lists extends InjectedController {
	def view = Action(Ok(views.html.pages.lists()))
}
