package models

import java.nio.file.Paths
import java.time.LocalDate
import play.api.Configuration
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.api.{Configuration,Logger}
import play.libs.mailer.{Email,MailerClient}
import scala.util.Try

object DeadLine {
	def demo(implicit cfg: Configuration) = cfg.get[Boolean]("ats4.demo")
	def isOK(implicit cfg: Configuration) = LocalDate.now.getMonthValue == 6 || demo
}

class Acceptor(implicit smtp: MailerClient, cfg: Configuration, db: Database) {
	val logger = Logger(classOf[Acceptor])
	def accept(scaned: Scaned, temp: TemporaryFile): Record = {
		val scored = scaned.next(Tables(temp.path.toString, scaned.sect).score)
		Conflict(scored)(db)
		val record = scored.next.get
		temp.moveFileTo(Paths.get(scored.file).toFile)
		val sougou = Sougou(record)
		if (sougou.nonEmpty) sougou.get.scored.next
		notify(record,sougou)
		logger.info(s"accepted: $record")
		record
	}
	def notify(record: Record, sougou: Option[Record]) {
		val host = cfg.get[String]("contest.host")
		val from = cfg.get[String]("contest.from")
		val repl = cfg.get[String]("contest.mail")
		val text = views.txt.pages.email(record,sougou).body.trim
		for(to <- Record.ofCall(record.call).map(_.mail).distinct) {
			val mail = new Email
			mail.setSubject(text.lines.toSeq.head.split("//")(0).trim)
			mail.setFrom("%s <%s>".format(host,from))
			mail.addTo("%s <%s>".format(record.call,to))
			mail.addBcc(repl)
			mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
			Try(smtp.send(mail)).recover{case e=>logger.error("mail error", e)}
		}
	}
}
