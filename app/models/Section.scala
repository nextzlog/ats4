package models

import scala.collection.mutable._
import java.util.Date

import qxsl.field.Call

case class Section(name: String, entries: List[Entry]) {
	val id = Rule.forName(name).id
	def rank(score: Int) = entries.indexWhere(_.score == score)
	def award(score: Int) = rank(score) <= awardlast
	def awards = entries.filter(entry => award(entry.score))
	def awardlast = Math.min(entries.size / 10, 6)
}

object Section {
	def all(): List[Section] = {
		val all = Storage.all
		val ents = new MutableList[Entry]
		ents ++= Bands.inner(all, Bands.bandLB).values
		ents ++= Bands.inner(all, Bands.bandHB).values
		ents ++= Bands.outer(all, Bands.bandAB).values
		ents ++= Joint.all
		val map = new HashMap[String, MutableList[Entry]]
		Rule.forName.keys.foreach(n => map(n) = new MutableList)
		ents.sortWith((e1, e2) => e1.score > e2.score) foreach {
			e => map(e.sect) += e
		}
		val sects = map.map{case (n, e) => Section(n, e.toList)}
		sects.toList.sortWith((c1, c2) => c1.id < c2.id)
	}
}
