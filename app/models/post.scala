package models

import java.nio.file.Files
import play.api.Logger
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.MailerClient
import qxsl.sheet.SheetOrTable
import scala.jdk.CollectionConverters._

class Acceptor(implicit smtp: MailerClient) {
	val sheets = new SheetOrTable()
	def push(post: Post, temps: Seq[TemporaryFile]): String = {
		val files = temps.map(_.toFile.toPath).map(Files.readAllBytes)
		val items = files.map(sheets.unpack).map(_.asScala).flatten
		Person.findAllByCall(post.call).foreach(_.delete())
		Record.findAllByCall(post.call).foreach(_.delete())
		Chrono.findAllByCall(post.call).foreach(_.delete())
		Logger(this.getClass).info(s"${post}")
		Chrono(post, items).save()
		Person(post).save()
		for(part <- post.take) Record(part, post, items).save()
		Logger(this.getClass).info(s"accepted: ${post}")
		Person.findAllByCall(post.call).foreach(new SendMail().send)
		post.call
	}
}
