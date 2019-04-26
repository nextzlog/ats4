package models

import java.time.LocalDate
import play.api.Configuration
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.api.{Configuration, Logger}
import play.libs.mailer.{Email, MailerClient}
import scala.util.Try

object DeadLine {
	def demo(implicit cfg: Configuration) = cfg.get[Boolean]("ats4.demo")
	def isOK(implicit cfg: Configuration) = LocalDate.now.getMonthValue == 6 || demo
}

class WorkFlow(implicit smtp: MailerClient, cfg: Configuration, db: Database) {
	def apply(user: User, temp: TemporaryFile): Post = {
		val path = temp.moveFileTo(user.file)
		val file = path.getFileName.toString
		val summ = QSOs(path.toString).summ(user)
		val post = user.post.copy(file=file, cnt=summ.calls, mul=summ.mults)
		Conflict(post)(db)
		Logger.info(s"accept: $post")
		val postAllBands = AllBands(post)
		postAllBands.foreach(_.insert)
		val mail = new SendMail
		mail(post,postAllBands)
		post.insert
	}
}

class SendMail(implicit smtp: MailerClient, cfg: Configuration, db: Database) {
	def apply(post: Post, postAllBands: Option[Post]) {
		val host = cfg.get[String]("contest.host")
		val repl = cfg.get[String]("contest.repl")
		val text = views.txt.pages.email(post,postAllBands).body.trim
		for(to <- Post.ofCall(post.call).map(_.mail).distinct) {
			val mail = new Email
			mail.setSubject(text.lines.toSeq.head)
			mail.setFrom("%s <%s>".format(host,repl))
			mail.addTo("%s <%s>".format(post.call,to))
			mail.addBcc(repl)
			mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString("\n"))
			Try(smtp.send(mail)).recover{case ex=>Logger.error("mail error",ex)}
		}
	}
}
