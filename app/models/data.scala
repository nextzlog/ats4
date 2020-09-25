package models

import anorm.{Macro, SQL, SqlStringInterpolation}
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import play.api.Logger
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.MailerClient
import scala.util.Try
import views.txt.pages.excel

class TableLoader(path: String, sect: String) {
	import java.nio.file.{Files, Paths}
	def bytes = Files.readAllBytes(Paths.get(path))
	def sheet = new qxsl.sheet.SheetManager().unpack(bytes)
	def naked = new qxsl.table.TableManager().decode(bytes)
	def strip = new qxsl.table.TableManager().decode(sheet)
	val score = Sections.find(sect).summarize(Try(naked).getOrElse(strip))
}

class Scoring(implicit db: Database, smtp: MailerClient) {
	def push(post: Post, temp: TemporaryFile): Team = {
		val file = Storage.file(post.team.call).toString
		temp.moveFileTo(Paths.get(file).toFile)
		val games = post.games(file)
		Book.del(post.team)
		Book.add(post.team)
		games.foreach(Book.del)
		games.foreach(Book.add)
		Logger(post.getClass).info(s"accepted: $post")
		new SendMail().send(post.team)
		Book.dump
		post.team
	}
}

case class Team(call: String, name: String, addr: String, mail: String, comm: String) {
	def insert = SQL"""insert into TEAMS values(NULL,${CALL.value},$name,$addr,$mail,$comm)"""
	def delete = SQL"""delete from TEAMS where call regexp ${CALL.strip().concat("(/.*)?")}"""
	def CALL = new qxsl.extra.field.Call(this.call)
}

case class Game(call: String, sect: String, city: String, score: Int, total: Int, file: String) {
	def insert = SQL"""insert into GAMES values(NULL,${CALL.value},$sect,$city,$score,$total,$file)"""
	def delete = SQL"""delete from GAMES where call regexp ${CALL.strip().concat("(/.*)?")}"""
	def rank(implicit db: Database) = Sect(sect).game.sortBy(- _.total).indexWhere(_.total == total)
	def best(implicit db: Database) = rank(db) < math.min(7, math.ceil(Sect(sect).game.length * .1))
	def CALL = new qxsl.extra.field.Call(this.call)
	def part = Part(sect, city)
}

case class Call(call: String) {
	def team(implicit db: Database) = db.withConnection(implicit c => Book.teamsOfCall(call).as(Book.team.*))
	def game(implicit db: Database) = db.withConnection(implicit c => Book.gamesOfCall(call).as(Book.game.*))
	def post(implicit db: Database) = Try {
		val games = this.game.map(_.part).map(p => p.code -> p).toMap
		PostForm.fill(Post(team.head, Sections.order.map(games.get)))
	}.getOrElse(PostForm)
}

case class Sect(sect: String) {
	def game(implicit db: Database) = db.withConnection(implicit c => Book.gamesOfSect(sect).as(Book.game.*))
}

object Book {
	val team = Macro.namedParser[Team]
	val game = Macro.namedParser[Game]
	def dump(implicit db: Database) = Files.write(Storage.file, excel().body.trim.getBytes(UTF_8))
	def teamsOfCall(call: String) = SQL"select * from TEAMS where call=$call"
	def gamesOfCall(call: String) = SQL"select * from GAMES where call=$call"
	def gamesOfSect(sect: String) = SQL"select * from GAMES where sect=$sect"
	def add(p: Team)(implicit db: Database) = db.withConnection(implicit c => p.insert.executeInsert())
	def add(e: Game)(implicit db: Database) = db.withConnection(implicit c => e.insert.executeInsert())
	def del(p: Team)(implicit db: Database) = db.withConnection(implicit c => p.delete.executeUpdate())
	def del(e: Game)(implicit db: Database) = db.withConnection(implicit c => e.delete.executeUpdate())
	def all(implicit db: Database) = db.withConnection(implicit c => SQL"select * from TEAMS".as(team.*))
}
