package models

import play.api.Logger
import play.libs.mailer.{Email, MailerClient}

import org.apache.commons.mail.EmailException

class SendMail(implicit smtp: MailerClient) {
	def remind(call: String) = StationData.findAllByCall(call).map(send).mkString
	def send(station: StationData) = util.Try {
		val mail = new Email
		val text = views.txt.pages.email(station.call).body.trim
		mail.setFrom("%s <%s>".format(Rule.rule.host, Rule.rule.mail))
		mail.addTo("%s <%s>".format(station.call, station.mail))
		mail.addBcc(Rule.rule.mail)
		mail.setSubject(text.linesIterator.toSeq.head.split(";").head.trim)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		smtp.send(mail)
	}.recover {
		case ex: EmailException => Logger(classOf[SendMail]).error("MAIL ERROR!", ex)
	}
}
