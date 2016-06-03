package models

import anorm._
import anorm.SqlParser._
import play.api.db._
import play.api.Play.current

import scala.language.postfixOps

import java.util.Date

import qxsl.field.Call

object Storage {
	def insert(entry: Entry) = DB.withConnection(implicit conn => SQL("""
		insert into entries(
			call,
			city,
			mobi,
			band,
			mode,
			name,
			addr,
			mail,
			comm,
			time,
			calls,
			mults
		) values (
			{call},
			{city},
			{mobi},
			{band},
			{mode},
			{name},
			{addr},
			{mail},
			{comm},
			{time},
			{calls},
			{mults}
		)
	""").on(
		'call  -> entry.call.getValue,
		'city  -> entry.city.getValue,
		'mobi  -> entry.mobi,
		'band  -> entry.band,
		'mode  -> entry.mode,
		'name  -> entry.name,
		'addr  -> entry.addr,
		'mail  -> entry.mail,
		'comm  -> entry.comm,
		'time  -> entry.time,
		'calls -> entry.calls,
		'mults -> entry.mults
	).executeUpdate)

	val entry = {
		get[String]("call") ~
		get[String]("city") ~
		get[String]("mobi") ~
		get[String]("band") ~
		get[String]("mode") ~
		get[String]("name") ~
		get[String]("addr") ~
		get[String]("mail") ~
		get[Option[String]]("comm") ~
		get[Date]("time") ~
		get[Int]("calls") ~
		get[Int]("mults") map {case call ~ city ~ mobi ~ band ~ mode ~ name ~ addr ~ mail ~ comm ~ time ~ calls ~ mults
			=> Entry(Prof(call, city, mobi, band, mode, name, addr, mail, comm), time, calls, mults)
		}
	}

	def all = DB.withConnection(implicit conn => SQL("""select * from entries order by id""").as(Storage.entry *))
	def enabled = Bands.inner(Storage.all, Bands.bandAB).values.toList.sortWith((e1, e2) => e1.time.before(e2.time))

	def mails(call: Call) = Storage.all.filter(_.call == call).map(_.mail).toSet

	def updateScore(entry: Entry, calls: Int, mults: Int) = DB.withConnection(implicit conn => SQL("""
		update entries set calls={calls}, mults={mults} where call={call} and time={time}
	""").on(
		'call  -> entry.call.getValue,
		'time  -> entry.time,
		'calls -> calls,
		'mults -> mults
	).executeUpdate)
}
