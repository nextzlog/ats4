package models

import java.time.{LocalDate, ZoneId}
import java.util.{List => JList}

import qxsl.ruler.{Absence, RuleKit}

import scala.jdk.CollectionConverters._
import scala.util.Try
import scala.util.chaining._

import play.api.Logger

object Rule {
	lazy val rule = Try(this.load).tap(_.failed.foreach(warn)).get
	def groups = rule.asScala.map(_.code).toSeq.distinct
	def load = RuleKit.load("/application.rb").contest()
	def warn(ex: Throwable) = Logger("rule").error("bad rule", ex)
	def absent(sect: String) = rule.section(sect).isInstanceOf[Absence]
	def cities(sect: String) = rule.section(sect).getCityList().asScala
	def limit(code: String) = rule.limitMultipleEntry(code)
	def conflict(sects: Seq[String]) = rule.conflict(sects.map(rule.section).filterNot(_.isAbsence).toArray)
}

object Rank {
	def glad(r: RankingData) = r.rule.win(r.rate, sort(r).map(_.rate).toArray)
	def sort(r: RankingData) = RankingData.findAllBySect(r.sect).toSeq.sortBy(-_.rate)
	def rank(r: RankingData) = sort(r).indexWhere(_.rate == r.rate)
}

object Schedule {
	lazy val year = LocalDate.now.getYear
	lazy val date = Rule.rule.getStartDay(year)
	lazy val dead = Rule.rule.getDeadLine(year)
	def finish = Rule.rule.finish(year, ZoneId.systemDefault())
	def accept = Rule.rule.accept(year, ZoneId.systemDefault())
}
