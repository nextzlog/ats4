package models

import java.io.InputStreamReader
import java.time.LocalDate
import java.util.{List=>JList}
import play.api.Logger
import qxsl.ruler.{RuleKit, Section}
import scala.jdk.CollectionConverters._
import scala.util.Try
import scala.util.chaining._

object Rule {
	def path = "/rule.rb"
	def stream = getClass.getResourceAsStream(path)
	def reader = new InputStreamReader(this.stream)
	def load = RuleKit.load("ruby").contest(reader)
	def warn(ex: Throwable) = Logger("rule").error("bad rule", ex)
	lazy val rule = Try(this.load).tap(_.failed.foreach(warn)).get
}

object Rank {
	def sort(r: Record) = Record.findAllBySect(r.sect).toSeq.sortBy(-_.total)
	def glad(r: Record) = rank(r) < math.min(7, math.ceil(sort(r).size * .1))
	def rank(r: Record) = sort(r).indexWhere(_.total == r.total)
}

object HostData {
	def host = Rule.rule.getHost()
	def link = Rule.rule.getLink()
	def mail = Rule.rule.getMail()
	def name = Rule.rule.getName()
}

object Schedule {
	lazy val year = LocalDate.now.getYear
	lazy val date = Rule.rule.getStartDay(year)
	lazy val dead = Rule.rule.getDeadLine(year)
	def isOK = LocalDate.now.isBefore(dead.plusDays(1))
}

object Subtests {
	def groups = Rule.rule.getSections.asScala.groupBy(_.getCode).toSeq.sortBy(_._1)
}

object CityBase {
	def all = Rule.rule.get("CITIES").asInstanceOf[JList[_]].asScala.map(_.toString)
}
