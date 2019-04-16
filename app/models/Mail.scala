package models

import play.api.Logger
import play.api.db.Database
import play.libs.mailer.{Email, MailerClient}
import scala.util.Try

class Mail(implicit smtp: MailerClient, db: Database) {
	def sendAcceptMail(post: Post, postAllBands: Option[Post]) {
		for(to <- Post.ofCall(post.call).map(_.mail).distinct) {
			val mail = new Email
			mail.setSubject(Conf.subj)
			mail.setFrom("%s <%s>".format(Conf.host, Conf.repl))
			mail.addTo("%s <%s>".format(post.call, to))
			mail.addBcc(Conf.repl)
			mail.setBodyText(views.txt.email(post, postAllBands).body.trim)
			Try(smtp.send(mail)).recover{case ex=>Logger.error("mail error",ex)}
		}
	}
}
