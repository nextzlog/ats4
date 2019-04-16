package models

import anorm.{Macro, SQL, SqlStringInterpolation}
import java.io.File
import java.lang.{String => S}
import play.api.db.Database

case class Post(id: Long, call: S, disp: S, city: S, sect: S, name: S, addr: S, mail: S, comm: S, cnt: Int, mul: Int, elog: S) {
	def insert(implicit db: Database) = db.withConnection{implicit conn => 
		copy(id=SQL"insert into post values(NULL,$call,$disp,$city,$sect,$name,$addr,$mail,$comm,$cnt,$mul,$elog)".executeInsert().get)
	}
	def delete(implicit db: Database) = db.withConnection{implicit conn =>
		SQL"delete from post where id=$id".executeUpdate
	}
	def score = cnt * mul
}

object Post {
	val parser = Macro.namedParser[Post]
	def all(implicit db: Database) = db.withConnection(implicit conn => SQL"select * from post".as(parser.*))
	def forId(id: Long)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL"select * from post where id=$id".as(parser.*)
	}.headOption
	def ofSect(sect: S)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL"select * from post where sect=$sect".as(parser.*)
	}
	def ofCall(call: S)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL"select * from post where call=$call".as(parser.*)
	}
}
