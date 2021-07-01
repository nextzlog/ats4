package models

import java.lang.{String => S}
import java.util.{UUID => U}

import qxsl.model.Item
import qxsl.table.TableManager

import scala.{Int => I}
import scala.jdk.CollectionConverters._

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class SectionData(sect: S, city: S)

case class ContestData(station: StationData, ranking: Seq[SectionData]) {
	def apply(tick: SectionData) = {
		val sum = LogBookData.findAllByCall(station.call).head.test(tick.sect)
		RankingData(
			call = station.call,
			sect = tick.sect,
			city = tick.city,
			mark = sum.score(),
			rate = sum.total(),
			code = Rule.rule.section(tick.sect).code()
		)
	}
}

case class RankingData(call: S, sect: S, city: S, mark: I, rate: I, code: S) extends ActiveRecord {
	def rule = Rule.rule.section(sect)
	def zero = !Rule.absent(sect) && mark == 0
	def json = RankingJson(call = call, score = mark, total = rate)
}

case class StationData(call: S, name: S, post: S, mail: S, note: S, uuid: U) extends ActiveRecord {
	def apply(seq: Seq[Item]) = LogBookData(call = call, data = new TableManager().encode(seq.asJava))
}

case class LogBookData(call: S, data: Array[Byte]) extends ActiveRecord {
	def test(s: String) = Rule.rule.section(s).summarize(list.asJava)
	def list = new TableManager().decode(data).asScala
}

case class RawBookData(call: S, data: Array[Byte]) extends ActiveRecord

object SectionData {
	def fill(call: String) = Subtests.groups.map(g => RankingData.fill(call, g._1).map(from)).flatten
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

object RawBookData extends ActiveRecordCompanion[RawBookData] {
	def findByID(id: Long) = this.findBy("id", id)
	def findAllByCall(call: S) = this.findAllBy("call", call).toList
}

object Tables extends ActiveRecordTables with PlaySupport {
	val stations = table[StationData]
	val rankings = table[RankingData]
	val logbooks = table[LogBookData]
	val rawbooks = table[RawBookData]
	on(stations)(c => declare(c.note is (dbType("clob"))))
	on(logbooks)(c => declare(c.data is (dbType("blob"))))
	on(rawbooks)(c => declare(c.data is (dbType("blob"))))
}
