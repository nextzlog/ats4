package models

import play.api.{Configuration, Logger}
import play.api.db.Database
import play.libs.mailer.{Email, MailerClient}
import scala.util.Try

class Mail(implicit smtp: MailerClient, cfg: Configuration, db: Database) {
	def sendAcceptMail(post: Post, postAllBands: Option[Post]) {
		val host = cfg.get[String]("contest.host")
		val repl = cfg.get[String]("contest.repl")
		val subj = cfg.get[String]("contest.subj")
		for(to <- Post.ofCall(post.call).map(_.mail).distinct) {
			val mail = new Email
			mail.setSubject(subj)
			mail.setFrom("%s <%s>".format(host,repl))
			mail.addTo("%s <%s>".format(post.call,to))
			mail.addBcc(repl)
			mail.setBodyText(views.txt.pages.email(post,postAllBands)(cfg).body)
			Try(smtp.send(mail)).recover{case ex=>Logger.error("mail error",ex)}
		}
	}
}
