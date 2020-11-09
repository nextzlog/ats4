package models

import java.io.InputStreamReader
import java.time.LocalDate
import java.util.{List => JList}

import qxsl.ruler.RuleKit

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
}

object Rank {
	def sort(r: Record) = Record.findAllBySect(r.sect).toSeq.sortBy(-_.total)
	def glad(r: Record) = rank(r) < math.min(7, math.ceil(sort(r).size * .1))
	def rank(r: Record) = sort(r).indexWhere(_.total == r.total)
}

object HostData {
	def host = Rule.rule.host()
	def link = Rule.rule.link()
	def mail = Rule.rule.mail()
	def name = Rule.rule.name()
}

object Schedule {
	lazy val year = LocalDate.now.getYear
	lazy val date = Rule.rule.getStartDay(year)
	lazy val dead = Rule.rule.getDeadLine(year)
	def isOK = LocalDate.now.isBefore(dead.plusDays(1))
}

object Subtests {
	def groups = Rule.rule.asScala.groupBy(_.code).toSeq.sortBy(_._1)
}

object CityBase {
	def all = Rule.rule.get("CITYDB").asInstanceOf[JList[_]].asScala.map(_.toString)
}
