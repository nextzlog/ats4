package models

import anorm.{Macro, SQL}
import play.api.db.Database

case class Post(call: String, disp: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String, cnt: Int, mul: Int) {
	def insert(implicit db: Database) = db.withConnection{implicit conn => 
		SQL("""insert into
			post  ( call,  disp,  city,  sect,  name,  addr,  mail,  comm,  cnt,  mul)
			values({call},{disp},{city},{sect},{name},{addr},{mail},{comm},{cnt},{mul})""").on(
			'call -> call,
			'disp -> disp,
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
		SQL("delete from post where call={call} and sect={sect} and cnt={cnt}").on('call->call, 'sect->sect, 'cnt->cnt).executeUpdate
	}
	def score = cnt * mul
}

object Post {
	val parser = Macro.namedParser[Post]
	def all(implicit db: Database) = db.withConnection(implicit conn => SQL("select * from post order by id").as(parser.*)).sortBy(_.call)
	def ofSect(sect: String)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL("select * from post where sect={sect} order by id").on('sect->sect).as(parser.*)
	}.sortBy(_.score).reverse
	def ofCall(call: String)(implicit db: Database) = db.withConnection{implicit conn =>
		SQL("select * from post where call={call} or call like {like} order by id").on('call->call,'like->s"${call}/%").as(parser.*)
	}.sortBy(_.call)
}
