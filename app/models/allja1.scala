package models

import java.time.temporal.TemporalAdjusters.dayOfWeekInMonth
import java.time.{DayOfWeek,LocalDate,Month}
import play.api.Configuration
import qxsl.extra.field.City
import qxsl.ruler.{RuleKit,Section}
import scala.jdk.CollectionConverters._

object Schedule {
	val year = LocalDate.now.getYear
	val date = LocalDate.of(year, Month.JUNE, 1).`with`(dayOfWeekInMonth(3, DayOfWeek.SUNDAY))
	val dead = LocalDate.of(year, Month.JULY, 1).`with`(dayOfWeekInMonth(2, DayOfWeek.SUNDAY))
	def rule(implicit cfg: Configuration) = cfg.get[String]("test.rule").format(year - 1988)
	def demo(implicit cfg: Configuration) = cfg.get[Boolean]("ats4.demo")
	def isOK(implicit cfg: Configuration) = LocalDate.now.isBefore(dead.plusDays(1)) || demo
}

object Sections {
	val ja1 = new RuleKit().contest("allja1.lisp")
	val all = ja1.asScala.toSeq
	val SinHBs = all.filter(_.getCode == "Sin1").toSeq
	val SinLBs = all.filter(_.getCode == "Sin2").toSeq
	val SinDGs = all.filter(_.getCode == "Sin3").toSeq
	val MulABs = all.filter(_.getCode == "Mul1").toSeq
	val MulDGs = all.filter(_.getCode == "Mul2").toSeq
	val order = Seq("Sin1","Sin2","Sin3","Mul1","Mul2")
	def forName(name: String) = all.filter(_.getName == name).head
	def joint(sects: Seq[Section]): Option[Section] = {
		if(sects.exists(_.getName.contains("1エリア外 個人"))) return Some(forName("1エリア外 個人 総合 部門"))
		if(sects.exists(_.getName.contains("1エリア内 個人"))) return Some(forName("1エリア内 個人 総合 部門"))
		if(sects.exists(_.getName.contains("1エリア外 団体"))) return Some(forName("1エリア外 団体 総合 部門"))
		if(sects.exists(_.getName.contains("1エリア内 団体"))) return Some(forName("1エリア内 団体 総合 部門"))
		return None
	}
}

object CallArea {
	val total = City.all("jarl").asScala.filter(_.getFullPath.size == 2).map(_.getFullName).toSeq.filterNot(_ == "東京都小笠原")
	val area1 = City.all("area").asScala.filter(_.getFullPath.get(2) == "関東").map(_.getCode).toSeq
}
