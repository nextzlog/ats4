package models

import play.api.Play.current
import play.api.Play.configuration

object Contest {
	val name = configuration.getString("ats3.contest.name").get
	val what = configuration.getString("ats3.contest.what").get
	val host = configuration.getString("ats3.contest.host").get
	val mail = configuration.getString("ats3.contest.mail").get
	val site = configuration.getString("ats3.contest.site").get
	val rule = configuration.getString("ats3.contest.rule").get
	val ats3 = configuration.getString("ats3.contest.ats3").get
}
