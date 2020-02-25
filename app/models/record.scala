package models

import anorm.{Macro, SQL, SqlStringInterpolation}
import play.api.db.Database
import scala.util.Try

case class Major(call: String, name: String, addr: String, mail: String, comm: String, file: String) {
	def query = SQL"insert into majors values(NULL,$call,$name,$addr,$mail,$comm,$file)"
	def push(implicit db: Database) = db.withConnection(implicit c=>query.executeInsert())
	def elim(implicit db: Database) = db.withConnection(implicit c=>SQL"delete from majors where call=$call".executeUpdate)
}

case class Minor(call: String, sect: String, city: String, denom: Int, score: Int, mults: Int) {
	def query = SQL"insert into minors values(NULL,$call,$sect,$city,$denom,$score,$mults)"
	def push(implicit db: Database) = db.withConnection(implicit c=>query.executeInsert())
	def elim(implicit db: Database) = db.withConnection(implicit c=>SQL"delete from minors where call=$call".executeUpdate)
	def total = math.floor(score * mults.toDouble / denom).toInt
	def place(implicit db: Database) = Minor.ofSect(sect).sortBy(-_.total).indexWhere(_.total == total)
	def award(implicit db: Database) = place <= math.min(6, math.floor(Minor.ofSect(sect).length * .1))
}

object Major {
	val parser = Macro.namedParser[Major]
	def all(implicit db: Database) = db.withConnection {
		implicit c => SQL"select * from majors".as(parser.*)
	}
	def ofCall(call: String)(implicit db: Database) = db.withConnection {
		implicit c => SQL"select * from majors where call=$call".as(parser.*)
	}.headOption
	def ofSect(sect: String)(implicit db: Database) = db.withConnection {
		implicit c => SQL"select * from majors where sect=$sect".as(parser.*)
	}
}

object Minor {
	val parser = Macro.namedParser[Minor]
	def ofCall(call: String)(implicit db: Database) = db.withConnection {
		implicit c => SQL"select * from minors where call=$call".as(parser.*)
	}
	def ofSect(sect: String)(implicit db: Database) = db.withConnection {
		implicit c => SQL"select * from minors where sect=$sect".as(parser.*)
	}
}
