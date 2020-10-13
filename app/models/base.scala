package models

import java.nio.charset.StandardCharsets.UTF_8
import qxsl.model.Item
import qxsl.table.TableManager
import scala.jdk.CollectionConverters._
import com.github.aselab.activerecord._
import com.github.aselab.activerecord.dsl._

case class Person(call: String, name: String, addr: String, mail: String, comm: String) extends ActiveRecord {
	def toInfo = Info(
		call = call,
		name = name,
		addr = addr,
		mail = mail,
		comm = comm
	)
}

case class Record(call: String, sect: String, city: String, score: Int, total: Int) extends ActiveRecord {
	def code = Rule.rule.getSection(sect).getCode()
	def toPart = Part(
		sect = sect,
		city = city
	)
}

case class Chrono(call: String, data: String) extends ActiveRecord {
	def list = new TableManager().getFactory("qxml").decode(data)
	def calc(s: String) = Rule.rule.getSection(s).summarize(list)
}

object Person extends ActiveRecordCompanion[Person] {
	def apply(post: Post): Person = Person(
		call = post.call,
		name = post.info.name,
		addr = post.info.addr,
		mail = post.info.mail,
		comm = post.info.comm
	)
	def toList = all.toList
	def findAllByCall(call: String) = Person.findAllBy("call", call).toList
}

object Record extends ActiveRecordCompanion[Record] {
	def apply(part: Part, post: Post, list: Seq[Item]): Record = {
		val sum = Rule.rule.getSection(part.sect).summarize(list.asJava)
		Record(
			call = post.call,
			sect = part.sect,
			city = part.city,
			score = sum.score(),
			total = sum.total()
		)
	}
	def findAllByCall(call: String) = Record.findAllBy("call", call).toList
	def findAllBySect(sect: String) = Record.findAllBy("sect", sect).toList
}

object Chrono extends ActiveRecordCompanion[Chrono] {
	val tables = new TableManager().getFactory("qxml")
	def apply(post: Post, items: Seq[Item]): Chrono = Chrono(
		call = post.call,
		data = new String(tables.encode(items.asJava), UTF_8)
	)
	def findAllByCall(call: String) = Chrono.findAllBy("call", call).toList
}

object Tables extends ActiveRecordTables with PlaySupport {
	val persons = table[Person]
	val records = table[Record]
	val chronos = table[Chrono]
	on(chronos)(c => declare(c.data is(dbType("text"))))
}
