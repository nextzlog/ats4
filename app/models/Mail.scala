package models

import play.api.Play.current
import play.api.Play.{configuration => conf}

import com.typesafe.plugin._

import qxsl.field.Call

object Mail {
	val from = conf.getString("ats3.mail.from").get
	val subj = conf.getString("ats3.mail.subj").get
	def accept(entry: Entry) = Storage.mails(entry.call).foreach(to => {
		val smtp = use[MailerPlugin].email
		smtp.setSubject(subj)
		smtp.setFrom("%s <%s>".format(Contest.host, from))
		smtp.setRecipient("%s <%s>".format(entry.call, to))
		smtp.send(views.txt.mail(entry).body.trim)
	})
}
