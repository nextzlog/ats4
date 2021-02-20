package models

import play.libs.mailer.{Email, MailerClient}

class SendMail(implicit smtp: MailerClient) {
	def remind(call: String) = Person.findAllByCall(call).map(send).mkString
	def send(person: Person) = {
		val mail = new Email
		val text = views.txt.pages.email(person.call).body.trim
		mail.setFrom("%s <%s>".format(Rule.rule.host, Rule.rule.mail))
		mail.addTo("%s <%s>".format(person.call, person.mail))
		mail.addBcc(Rule.rule.mail)
		mail.setSubject(text.linesIterator.toSeq.head.split(";").head.trim)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		smtp.send(mail)
		"OK"
	}
}
