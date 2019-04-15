package models

import anorm.{Macro, SQL}
import play.api.db.Database
import anorm.SqlStringInterpolation

case class Post(call: String, disp: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String, cnt: Int, mul: Int) {
	def insert(implicit db: Database) = db.withConnection{implicit conn => 
		SQL"insert into post values(NULL,$call,$disp,$city,$sect,$name,$addr,$mail,$comm,$cnt,$mul)".executeUpdate
	}
	def delete(implicit db: Database) = db.withConnection{implicit conn =>
		SQL"delete from post where call=$call and sect=$sect and cnt=$cnt".executeUpdate
	}
	def score = cnt * mul
}

object Post {
	val parser = Macro.namedParser[Post]
	def all(implicit db: Database) = db.withConnection(implicit conn => SQL"select * from post order by id".as(parser.*)).sortBy(_.call)
	def ofSect(sect: String)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL"select * from post where sect=$sect order by id".as(parser.*)
	}.sortBy(_.score).reverse
	def ofCall(call: String)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL"select * from post where call=$call order by id".as(parser.*)
	}.sortBy(_.call)
}
