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
import qxsl.sheet.Sheets
import qxsl.table.Tables
import scala.util.Try

object UserForm extends Form(Forms.mapping(
	"call" -> Forms.nonEmptyText,
	"city" -> Forms.nonEmptyText,
	"sect" -> Forms.nonEmptyText,
	"name" -> Forms.nonEmptyText,
	"addr" -> Forms.nonEmptyText,
	"mail" -> Forms.email,
	"comm" -> Forms.text
)(User.apply)(User.unapply), Map.empty, Nil, None)

case class User(call: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String) {
	def disp = Normalizer.normalize(call.toUpperCase, Normalizer.Form.NFKC)
	def safe = disp.split('/').head.replaceAll("[^0-9A-Z]", "")
	val file = QSOs.file(QSOs.name(safe)) // safety against directory traversal
	def post = Post(-1,call=safe,disp=disp,city=city,sect=CallArea.sect(this),name=name,addr=addr,mail=mail,comm=comm)
}

case class Post(id: Long, call: S, disp: S, city: S, sect: S, name: S, addr: S, mail: S, comm: S, file: S = null, cnt: Int = 0, mul: Int = 0) {
	def delete(implicit db: Database) = db.withConnection(implicit c => SQL"delete from post where id=$id".executeUpdate)
	def insert(implicit db: Database) = db.withConnection{implicit c => 
		copy(id=SQL"insert into post values(NULL,$call,$disp,$city,$sect,$name,$addr,$mail,$comm,$file,$cnt,$mul)".executeInsert().get)
	}
	def score = cnt * mul
	def place(implicit db: Database) = Post.ofSect(sect).sortBy(-_.score).indexWhere(_.score == score)
	def award(implicit db: Database) = place <= math.min(6, math.floor(Post.ofSect(sect).length * .1))
}

object Post {
	val parser = Macro.namedParser[Post]
	def all(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post".as(parser.*))
	def forId(id: Long)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post where id=$id".as(parser.*)).headOption
	def ofSect(sect: S)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post where sect=$sect".as(parser.*))
	def ofCall(call: S)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post where call=$call".as(parser.*))
}

case class QSOs(path: String) {
	val sheets = new Sheets()
	val tables = new Tables()
	val source = Paths.get(path).toUri.toURL
	val sheet = Try(sheets.decode(source).get("LOGSHEET").getBytes)
	val table = Try(tables.decode(sheet.get)).getOrElse(tables.decode(source))
	def summ(user: User) = new Summary(table, Sections.forName(CallArea.sect(user)))
	def summ(post: Post) = new Summary(table, Sections.forName(post.sect))
}

object QSOs {
	val save = Paths.get(System.getProperty("user.dir")).resolve("rcvd")
	val dfmt = DateTimeFormatter.ofPattern("'%s'.yyyyMMdd.HHmmss.'log'")
	def file(name: String) = save.resolve(name).toFile
	def name(call: String) = LocalDateTime.now.format(dfmt).format(call)
	if(Files.notExists(save)) Files.createDirectories(save)
}
