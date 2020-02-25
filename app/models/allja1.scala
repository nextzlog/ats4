package models

import java.time.temporal.TemporalAdjusters.dayOfWeekInMonth
import java.time.{DayOfWeek,LocalDate,Month}
import play.api.Configuration
import qxsl.extra.field.City
import qxsl.ruler.Contest
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
	val all = Contest.defined("allja1.lisp").asScala.toSeq
	val HBs = all.filter(_.getCode == "アナログ ハイバンド部門").toSeq
	val LBs = all.filter(_.getCode == "アナログ ローバンド部門").toSeq
	val DBs = all.filter(_.getCode == "デジタル 全周波数帯部門").toSeq
	val ABs = all.filter(_.getCode == "アナログ 全周波数帯部門").toSeq
	def forName(name: String) = all.filter(_.getName == name).head
}

object CallArea {
	val total = City.all("jarl").asScala.filter(_.getFullPath.size == 2).map(_.getFullName).toSeq.filterNot(_ == "東京都小笠原")
	val area1 = City.all("area").asScala.filter(_.getFullPath.get(2) == "関東").map(_.getCode).toSeq
}
