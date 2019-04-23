package models

import anorm.{Macro, SQL, SqlStringInterpolation}
import java.lang.{String => S}
import play.api.db.Database

case class Post(id: Long, call: S, disp: S, city: S, sect: S, name: S, addr: S, mail: S, comm: S, file: S = null, cnt: Int = 0, mul: Int = 0) {
	def delete(implicit db: Database) = db.withConnection(implicit c => SQL"delete from post where id=$id".executeUpdate)
	def insert(implicit db: Database) = db.withConnection{implicit c => 
		copy(id=SQL"insert into post values(NULL,$call,$disp,$city,$sect,$name,$addr,$mail,$comm,$file,$cnt,$mul)".executeInsert().get)
	}
	def score = cnt * mul
}

object Post {
	val parser = Macro.namedParser[Post]
	def all(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post".as(parser.*))
	def forId(id: Long)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post where id=$id".as(parser.*)).headOption
	def ofSect(sect: S)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post where sect=$sect".as(parser.*))
	def ofCall(call: S)(implicit db: Database) = db.withConnection(implicit c => SQL"select * from post where call=$call".as(parser.*))
}
