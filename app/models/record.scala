package models

import anorm.{Macro, SQL, SqlStringInterpolation}
import java.lang.{String => S}
import java.nio.file.{Files, Paths}
import java.text.Normalizer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import play.api.data.{Form, Forms}
import play.api.db.Database
import qxsl.ruler.Summary
import scala.util.Try

object Format extends Form(Forms.mapping(
	"disp" -> Forms.nonEmptyText,
	"city" -> Forms.nonEmptyText,
	"part" -> Forms.nonEmptyText,
	"name" -> Forms.nonEmptyText,
	"addr" -> Forms.nonEmptyText,
	"mail" -> Forms.email,
	"comm" -> Forms.text
)(Scaned.apply)(Scaned.unapply), Map.empty, Nil, None)

case class Scaned(disp: S, city: S, part: S, name: S, addr: S, mail: S, comm: S) {
	def call = Normalizer.normalize(disp.toUpperCase, Normalizer.Form.NFKC)
	def area = "1エリア%s".format(if(CallArea.inner.contains(city)) "内" else "外")
	def sect = s"$area $part"
	def next(summ: Summary) = Scored(this, summ.score, summ.mults)
}

case class Tables(path: String, sect: String) {
	def bytes = Files.readAllBytes(Paths.get(path))
	def sheet = new qxsl.sheet.Sheets().decode(bytes).get("LOGSHEET").getBytes()
	def table = new qxsl.table.Tables().decode(util.Try(sheet).getOrElse(bytes))
	val score = Sections.forName(sect).summarize(table)
}

case class Scored(scaned: Scaned, calls: Int, mults: Int) {
	def call = scaned.call
	def city = scaned.city
	def sect = scaned.sect
	def comm = scaned.comm
	def name = scaned.name
	def addr = scaned.addr
	def mail = scaned.mail
	// countermeasure against directory-traversal attacks:
	def safe = call.split('/').head.replaceAll("[^0-9A-Z]","")
	def path = Files.createDirectories(Paths.get("ats4.rcvd"))
	def dfmt = DateTimeFormatter.ofPattern("'%s'.yyyyMMdd.HHmmss.'log'")
	val file = path.resolve(LocalDateTime.now.format(dfmt).format(safe)).toString
	def post = SQL"insert into posts values(NULL,$call,$city,$sect,$name,$addr,$mail,$comm,$file,$calls,$mults)"
	def next(implicit db: Database) = db.withConnection(implicit c => Record.forId(id=post.executeInsert().get))
}

case class Record(id: Long, call: S, city: S, sect: S, name: S, addr: S, mail: S, comm: S, file: S, calls: Int, mults: Int) {
	def score = calls * mults
	def place(implicit db: Database) = Record.ofSect(sect).sortBy(-_.score).indexWhere(_.score == score)
	def award(implicit db: Database) = place <= math.min(6, math.floor(Record.ofSect(sect).length * .1))
	def purge(implicit db: Database) = db.withConnection(implicit c=>SQL"delete from posts where id=$id".executeUpdate)
	def scaned = Scaned(call,city=city,part=sect.split(" ").tail.mkString(" "),name=name,addr=addr,mail=mail,comm=comm)
	def scored = Scored(scaned, calls=calls, mults=mults)
}

object Record {
	private val parser = Macro.namedParser[Record]
	def all(implicit db: Database) = db.withConnection(implicit c => SQL"select * from posts".as(parser.*))
	def forId(id: Long)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from posts where id=$id".as(parser.*)).headOption
	def ofSect(sect: S)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from posts where sect=$sect".as(parser.*))
	def ofCall(call: S)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from posts where call=$call".as(parser.*))
}
