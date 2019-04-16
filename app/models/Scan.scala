package models

import java.io.{ByteArrayInputStream, File, FileInputStream}
import java.text.SimpleDateFormat
import play.api.Logger
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.MailerClient
import qxsl.ruler.Summary
import qxsl.sheet.Sheets
import qxsl.table.Tables
import scala.collection.JavaConversions._
import scala.util.Try

case class Scan(prof: Prof, file: TemporaryFile)(implicit smtp: MailerClient, db: Database) {
	val date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date)
	val elog = new File(Conf.save, s"${prof.safeCall}.$date.log")
	file.moveTo(elog)
	Logger.info(s"stored: $elog")
	val sheet = Try(new ByteArrayInputStream(new Sheets().decode(new FileInputStream(elog))("LOGSHEET").getBytes))
	val table = new Tables().decode(sheet.getOrElse(new FileInputStream(elog)))
	val summ = new Summary(table, Conf.sect(prof.fullSect))
	val post = prof.post(summ)
	Logger.info(s"accept: $post")
	Dupe.delete(post)
	post.insert
	val postAllBands = HiLo.post(post)
	postAllBands.foreach(_.insert)
	new Mail().sendAcceptMail(post,postAllBands)
}
