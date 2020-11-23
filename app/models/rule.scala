package models

import java.io.InputStreamReader
import java.time.{LocalDate, ZoneId}
import java.util.{List => JList}

import qxsl.ruler.{Absence, RuleKit}

import scala.jdk.CollectionConverters._
import scala.util.Try
import scala.util.chaining._

import play.api.Logger

object Rule {
	def path = "/application.rb"
	def stream = getClass.getResourceAsStream(path)
	def reader = new InputStreamReader(this.stream)
	def load = RuleKit.forName("ruby").eval(reader).contest()
	def warn(ex: Throwable) = Logger("rule").error("bad rule", ex)
	lazy val rule = Try(this.load).tap(_.failed.foreach(warn)).get
	def absent(sect: String) = rule.section(sect).isInstanceOf[Absence]
}

object Rank {
	def sort(r: Record) = Record.findAllBySect(r.sect).toSeq.sortBy(-_.rate)
	def glad(r: Record) = rank(r) < math.min(7, math.ceil(sort(r).size * .1))
	def rank(r: Record) = sort(r).indexWhere(_.rate == r.rate)
}

object Schedule {
	lazy val year = LocalDate.now.getYear
	lazy val date = Rule.rule.getStartDay(year)
	lazy val dead = Rule.rule.getDeadLine(year)
	def open = Rule.rule.openResults(year, ZoneId.systemDefault())
	def isOK = Rule.rule.openEntries(year, ZoneId.systemDefault())
}

object Subtests {
	def groups = Rule.rule.asScala.groupBy(_.code).toSeq.sortBy(_._1)
}

object CityBase {
	def all = Rule.rule.get("CITYDB").asInstanceOf[JList[_]].asScala.map(_.toString)
}
