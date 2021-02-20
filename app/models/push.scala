package models

import java.nio.file.Files
import java.util.UUID

import qxsl.sheet.SheetOrTable

import scala.jdk.CollectionConverters._

import play.api.{Configuration, Logger}
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.MailerClient

class Acceptor(implicit smtp: MailerClient) {
	val decoder = new SheetOrTable()
	def push(post: Client, files: Seq[TemporaryFile]): String = {
		val bytes = files.map(_.toFile.toPath).map(Files.readAllBytes)
		val items = bytes.map(decoder.unpack).map(_.asScala).flatten
		Person.findAllByCall(post.person.call).foreach(_.delete())
		Record.findAllByCall(post.person.call).foreach(_.delete())
		Report.findAllByCall(post.person.call).foreach(_.delete())
		post.person.save()
		post.person(items).save()
		for (t <- post.record) post(t).save()
		Logger(this.getClass).info(s"accept: $post")
		Person.findAllByCall(post.person.call).foreach(new SendMail().send)
		post.person.call
	}
}

class Receiver(uuid: UUID)(implicit cfg: Configuration) {
	val decoder = new SheetOrTable()
	def push(data: Array[Byte]): String = {
		val call = Person.findAllByUUID(uuid).head.call
		val post = Client.fill(call)
		val list = Report.findAllByCall(call).head.list
		val diff = decoder.unpack(data.tail).asScala
		list --= diff.take(data.head.toInt & 0xFF)
		list ++= diff.drop(data.head.toInt & 0xFF)
		Record.findAllByCall(call).foreach(_.delete())
		Report.findAllByCall(call).foreach(_.delete())
		post.person(list.toSeq).save()
		for (t <- post.record) post(t).save()
		Logger(this.getClass).info(s"accept: $post")
		RecordJson.latest
	}
}
