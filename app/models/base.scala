package models

import java.lang.{String => S}
import java.util.{UUID => U}

import qxsl.model.Item
import qxsl.table.TableManager

import scala.{Int => I}
import scala.jdk.CollectionConverters._

import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Ticket(sect: S, city: S)

case class Client(person: Person, record: Seq[Ticket]) {
	def apply(tick: Ticket) = {
		val sum = Report.findAllByCall(person.call).head.rate(tick.sect)
		Record(
			call = person.call,
			sect = tick.sect,
			city = tick.city,
			mark = sum.score(),
			rate = sum.total(),
			code = Rule.rule.section(tick.sect).code()
		)
	}
}

case class Record(call: S, sect: S, city: S, mark: I, rate: I, code: S) extends ActiveRecord {
	def rule = Rule.rule.section(sect)
	def zero = !Rule.absent(sect) && mark == 0
	def json = RecordJson(call = call, score = mark, total = rate)
}

case class Person(call: S, name: S, post: S, mail: S, note: S, uuid: U) extends ActiveRecord {
	def apply(seq: Seq[Item]) = Report(call = call, data = new TableManager().encode(seq.asJava))
}

case class Report(call: S, data: Array[Byte]) extends ActiveRecord {
	def rate(s: String) = Rule.rule.section(s).summarize(list.asJava)
	def list = new TableManager().decode(data).asScala
}

object Ticket {
	def fill(call: String) = Subtests.groups.map(g => Record.fill(call, g._1).map(from)).flatten
	def from(post: Record) = Ticket(sect = post.sect, city = post.city)
}

object Client {
	def fill(call: String) = Client(person = Person.fill(call), record = Ticket.fill(call))
}

object Person extends ActiveRecordCompanion[Person] {
	def uuid: U = U.randomUUID() match {
		case id if findAllByUUID(id).isEmpty => id
		case id => this.uuid
	}
	def fill(call: String) = findAllByCall(call).head
	def findAllByCall(call: S) = this.findAllBy("call", call).toList
	def findAllByUUID(uuid: U) = this.findAllBy("uuid", uuid).toList
}

object Record extends ActiveRecordCompanion[Record] {
	def fill(call: String, code: String) = findAllByCall(call).find(_.code == code)
	def findAllByCall(call: S) = this.findAllBy("call", call).toList
	def findAllBySect(sect: S) = this.findAllBy("sect", sect).toList
}

object Report extends ActiveRecordCompanion[Report] {
	def findAllByCall(call: S) = this.findAllBy("call", call).toList
}

object Tables extends ActiveRecordTables with PlaySupport {
	val persons = table[Person]
	val records = table[Record]
	val reports = table[Report]
	on(reports)(c => declare(c.data is (dbType("blob"))))
}
