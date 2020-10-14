package models

import play.libs.mailer.{Email, MailerClient}

class SendMail(implicit smtp: MailerClient) {
	def send(person: Person) = {
		val mail = new Email
		val text = views.txt.pages.email(person.call).body.trim
		mail.setFrom("%s <%s>".format(HostData.host, HostData.mail))
		mail.addTo("%s <%s>".format(person.call, person.mail))
		mail.addBcc(HostData.mail)
		mail.setSubject(text.linesIterator.toSeq.head.split(";").head.trim)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		smtp.send(mail)
		"OK"
	}
	def remind(call: String) = Person.findAllByCall(call).map(send).mkString
}
