package models

import play.api.Play.current
import play.api.Play.{configuration => conf}

import java.text.SimpleDateFormat
import java.util.Date

object TimeLimit {
	val dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
	val starting = dateFormat.parse(conf.getString("ats3.starting").get)
	val deadline = dateFormat.parse(conf.getString("ats3.deadline").get)
	def isOK: Boolean = {
		val today = new Date()
		if(starting.after(today))  return false
		if(deadline.before(today)) return false
		return true
	}
	def notOK = !isOK
	def remain = (deadline.getTime - new Date().getTime)
}
