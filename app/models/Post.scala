package models

import anorm.{Macro, SQL}
import play.api.db.Database

case class Post(call: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String, cnt: Int, mul: Int) {
	def insert(implicit db: Database) = db.withConnection{implicit conn => 
		SQL("""insert into
			post  ( call,  city,  sect,  name,  addr,  mail,  comm,  cnt,  mul)
			values({call},{city},{sect},{name},{addr},{mail},{comm},{cnt},{mul})""").on(
			'call -> call,
			'city -> city,
			'sect -> sect,
			'name -> name,
			'addr -> addr,
			'mail -> mail,
			'comm -> comm,
			'cnt  -> cnt,
			'mul  -> mul
		).executeUpdate
	}
	def delete(implicit db: Database) = db.withConnection{implicit conn =>
		SQL("delete from post where call={call} and sect={sect}").on('call->call, 'sect->sect).executeUpdate
	}
	def score = cnt * mul
}

object Post {
	val parser = Macro.namedParser[Post]
	def all(implicit db: Database) = db.withConnection(implicit conn => SQL("select * from post order by id").as(parser.*)).sortBy(_.call)
	def ofSect(sect: String)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL("select * from post where sect like {sect} order by id").on('sect->s"{sect}%").as(parser.*)
	}.sortBy(_.score).reverse
	def ofCall(call: String)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL("select * from post where call like {call} order by id").on('call->s"{name}%").as(parser.*)
	}.sortBy(_.call)
}
