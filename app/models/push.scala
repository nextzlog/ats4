package models

import java.nio.file.Files
import java.util.UUID

import qxsl.sheet.SheetOrTable

import scala.jdk.CollectionConverters._

import play.api.{Configuration, Logger}
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.MailerClient

class Verifier {
	val tables = new SheetOrTable()
	def load(file: TemporaryFile) = Files.readAllBytes(file.toFile.toPath)
	def test(file: TemporaryFile) = tables.unpack(load(file))
}

class Acceptor(implicit smtp: MailerClient) {
	def push(post: ContestData, files: Seq[TemporaryFile]): String = {
		val logs = files.map(_.toFile.toPath).map(Files.readAllBytes)
		StationData.findAllByCall(post.station.call).foreach(_.delete())
		RankingData.findAllByCall(post.station.call).foreach(_.delete())
		LogBookData.findAllByCall(post.station.call).foreach(_.delete())
		post.station.save()
		for (b <- logs) post.station(b).save()
		for (t <- post.ranking) post(t).save()
		StationData.findAllByCall(post.station.call).foreach(new SendMail().send)
		Logger(this.getClass).info(s"accept: $post")
		post.station.call
	}
}

class Receiver(uuid: UUID)(implicit cfg: Configuration) {
	val decoder = new SheetOrTable()
	def push(data: Array[Byte]): String = {
		val call = StationData.findAllByUUID(uuid).head.call
		val post = ContestData.fill(call)
		val list = LogBookData.findAllByCall(call).head.toList.toBuffer
		val diff = decoder.unpack(data.tail).asScala
		list --= diff.take(data.head.toInt & 0xFF)
		list ++= diff.drop(data.head.toInt & 0xFF)
		RankingData.findAllByCall(call).foreach(_.delete())
		LogBookData.findAllByCall(call).foreach(_.delete())
		post.station(list.toSeq).save()
		for (t <- post.ranking) post(t).save()
		Logger(this.getClass).info(s"accept: $post")
		RankingJson.latest
	}
}
