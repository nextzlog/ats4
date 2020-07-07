package models

import play.api.Logger
import play.api.db.Database
import play.libs.mailer.{Email, MailerClient}

class SendMail(implicit db: Database, smtp: MailerClient) {
	def send(team: Team) = {
		val mail = new Email
		val text = views.txt.pages.email(team).body.trim
		mail.setFrom("%s <%s>".format(Contest.host, Contest.mail))
		mail.addTo("%s <%s>".format(team.call, team.mail))
		mail.addBcc(Contest.mail)
		mail.setSubject(text.linesIterator.toSeq.head.split(";").head.trim)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		smtp.send(mail)
	}
	def sendAll(teams: Seq[Team]) = new Thread {
		val itvl = MailConf.itvl * 60 * 1000
		override def run() = for(team <- teams) {
			send(team)
			Thread.sleep(itvl)
		}
	}.start()
}
