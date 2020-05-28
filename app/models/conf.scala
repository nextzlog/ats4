package models

import java.io.InputStreamReader
import java.nio.file.{Files, Paths}
import java.time.{LocalDate, LocalDateTime}
import play.api.Configuration
import qxsl.extra.field.Call
import qxsl.ruler.{RuleKit, Section}

object Contest {
	def name(implicit cfg: Configuration) = cfg.get[String]("test.name")
	def host(implicit cfg: Configuration) = cfg.get[String]("test.host")
	def mail(implicit cfg: Configuration) = cfg.get[String]("test.mail")
	def site(implicit cfg: Configuration) = cfg.get[String]("test.site")
}

object Schedule {
	val year = LocalDate.now.getYear
	val date = Sections.ja1.invoke("starting", year).asInstanceOf[LocalDate]
	val dead = Sections.ja1.invoke("deadline", year).asInstanceOf[LocalDate]
	def rule(implicit cfg: Configuration) = cfg.get[String]("test.rule").format(year - 1988)
	def isOK(implicit cfg: Configuration) = LocalDate.now.isBefore(dead.plusDays(1))
}

object Sections {
	import scala.jdk.CollectionConverters._
	val ja1 = new RuleKit().contest(new InputStreamReader(getClass.getResourceAsStream("/rule.lisp")))
	val all = ja1.asScala.toSeq
	val SinHBs = all.filter(_.getCode == "Sin1").toSeq
	val SinLBs = all.filter(_.getCode == "Sin2").toSeq
	val SinDGs = all.filter(_.getCode == "Sin3").toSeq
	val MulABs = all.filter(_.getCode == "Mul1").toSeq
	val MulDGs = all.filter(_.getCode == "Mul2").toSeq
	val order = Seq("Sin1","Sin2","Sin3","Mul1","Mul2")
	def find(name: String) = all.filter(_.getName == name).head
	def joint(sects: Seq[Section]): Section = {
		import elva.Cons.wrap
		return find(ja1.invoke("総合部門の選択", wrap(sects.map(_.getName):_*)).toString)
	}
	def valid(part: Part): Boolean = {
		ja1.invoke("部門と運用地の検査", part.sect, part.city).asInstanceOf[Boolean]
	}
}

object Storage {
	import java.time.LocalDate
	import java.time.format.DateTimeFormatter.ofPattern
	def now = LocalDateTime.now.format(ofPattern("'%s'.yyyyMMdd.HHmmss.'log'"))
	def path(implicit cfg: Configuration) = Files.createDirectories(Paths.get(cfg.get[String]("ats4.rcvd")))
	def file(call: String)(implicit cfg: Configuration) = path.resolve(now.format(new Call(call).strip))
	def file(implicit cfg: Configuration) = path.resolve("report.csv")
}
