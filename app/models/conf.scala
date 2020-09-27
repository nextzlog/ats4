package models

import java.io.InputStreamReader
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, LocalDateTime}
import qxsl.extra.field.Call
import qxsl.ruler.{RuleKit, Section}

object Contest {
	lazy val name = Sections.ja1.get("NAME").asInstanceOf[String]
	lazy val host = Sections.ja1.get("HOST").asInstanceOf[String]
	lazy val mail = Sections.ja1.get("MAIL").asInstanceOf[String]
	lazy val site = Sections.ja1.get("SITE").asInstanceOf[String]
}

object Schedule {
	lazy val year = LocalDate.now.getYear
	lazy val date = Sections.ja1.invoke("starting", year).asInstanceOf[LocalDate]
	lazy val dead = Sections.ja1.invoke("deadline", year).asInstanceOf[LocalDate]
	lazy val rule = Sections.ja1.get("RULE").asInstanceOf[String].format(year - 1988)
	def isOK = LocalDate.now.isBefore(dead.plusDays(1))
}

object MailConf {
	lazy val itvl = Sections.ja1.get("MAIL_INTERVAL_MINUTES").asInstanceOf[Int]
}

object Sections {
	import scala.jdk.CollectionConverters._
	def conf = getClass.getResourceAsStream("/rule.rb")
	val ja1 = RuleKit.load("ruby").contest(new InputStreamReader(conf))
	val all = ja1.getSections().asScala.toSeq
	val SinHBs = all.filter(_.getCode == "Sin1").toSeq
	val SinLBs = all.filter(_.getCode == "Sin2").toSeq
	val SinDGs = all.filter(_.getCode == "Sin3").toSeq
	val MulABs = all.filter(_.getCode == "Mul1").toSeq
	val MulDGs = all.filter(_.getCode == "Mul2").toSeq
	val order = Seq("Sin1","Sin2","Sin3","Mul1","Mul2")
	def find(name: String) = all.filter(_.getName == name).head
	def joint(sects: Seq[Section]) = find(ja1.invoke("総合部門", sects.map(_.getName).toArray).toString)
	def valid(part: Part) = ja1.invoke("運用場所", part.sect, part.city).asInstanceOf[Boolean]
}

object Storage {
	import java.time.LocalDate
	import java.time.format.DateTimeFormatter.ofPattern
	def now = LocalDateTime.now.format(ofPattern("'%s'.yyyyMMdd.HHmmss.'log'"))
	def path = Files.createDirectories(Paths.get(Sections.ja1.get("OUTPUT").asInstanceOf[String]))
	def file(call: String) = path.resolve(now.format(new Call(call).strip)) // AGAINST PATH TRAVERSAL ATTACK
	def file = path.resolve(Sections.ja1.get("REPORT").asInstanceOf[String])
}
