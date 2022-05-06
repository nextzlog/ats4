package models

import java.lang.{String => S}
import java.util.{UUID => U}

import qxsl.model.Item
import qxsl.sheet.SheetOrTable
import qxsl.table.TableManager

import scala.{Int => I}
import scala.jdk.CollectionConverters._

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class SectionData(sect: S, city: S)

case class ContestData(station: StationData, ranking: Seq[SectionData]) {
	def apply(tick: SectionData) = {
		val rank = RankingData(
			call = station.call,
			sect = tick.sect,
			city = tick.city,
			mark = 0,
			rate = 0,
			code = Rule.rule.section(tick.sect).code()
		)
		val sum = rank.test(LogBookData.findAllByCall(station.call))
		rank.copy(mark = sum.score(), rate = sum.total())
	}
}

case class RankingData(call: S, sect: S, city: S, mark: I, rate: I, code: S) extends ActiveRecord {
	def rule = Rule.rule.section(sect)
	def zero = !Rule.absent(sect) && mark == 0
	def json = RankingJson(call = call, score = mark, total = rate)
	def test(logs: Seq[LogBookData]) = Rule.rule.section(sect).summarize(logs.map(_.toList).flatten.asJava)
}

case class StationData(call: S, name: S, post: S, addr: S, mail: S, note: S, uuid: U) extends ActiveRecord {
	def apply(data: Array[Byte]): LogBookData = LogBookData(call = call, data = data)
	def apply(data: Seq[Item]): LogBookData = this(new TableManager().encode(data.asJava))
}

case class LogBookData(call: S, data: Array[Byte]) extends ActiveRecord {
	def decode = new SheetOrTable().unpack(data).asScala
	def toList = scala.util.Try(decode).getOrElse(Seq())
	def toWarn = scala.util.Try(decode).failed.map(_.getCause.getMessage).toOption
}

object SectionData {
	def fill(call: String) = Rule.groups.sorted.map(g => RankingData.fill(call, g).map(from)).flatten
	def from(post: RankingData) = SectionData(sect = post.sect, city = post.city)
}

object ContestData {
	def fill(call: String) = ContestData(station = StationData.fill(call), ranking = SectionData.fill(call))
}

object StationData extends ActiveRecordCompanion[StationData] {
	def uuid: U = U.randomUUID() match {
		case id if findAllByUUID(id).isEmpty => id
		case id => this.uuid
	}
	def fill(call: String) = findAllByCall(call).head
	def findAllByCall(call: S) = this.findAllBy("call", call).toList
	def findAllByUUID(uuid: U) = this.findAllBy("uuid", uuid).toList
}

object RankingData extends ActiveRecordCompanion[RankingData] {
	def fill(call: String, code: String) = findAllByCall(call).find(_.code == code)
	def findAllByCall(call: S) = this.findAllBy("call", call).toList
	def findAllBySect(sect: S) = this.findAllBy("sect", sect).toList
}

object LogBookData extends ActiveRecordCompanion[LogBookData] {
	def findByID(id: Long) = this.findBy("id", id)
	def findAllByCall(call: S) = this.findAllBy("call", call).toList
}

object Tables extends ActiveRecordTables with PlaySupport {
	val stations = table[StationData]
	val rankings = table[RankingData]
	val logbooks = table[LogBookData]
	on(stations)(c => declare(c.note is (dbType("clob"))))
	on(logbooks)(c => declare(c.data is (dbType("blob"))))
}
