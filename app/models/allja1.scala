package models

import play.api.db.Database
import qxsl.field.City
import qxsl.ruler.Contest
import scala.collection.JavaConverters._

object Sections {
	val all = Contest.defined("allja1.lisp").asScala.toList
	def AM = all.map(_.getName).filter(_.matches(""".*(1\.9|3\.5|7).*"""))
	def PM = all.map(_.getName).filter(_.matches(""".*(14|21|28|50).*"""))
	def RC = all.map(_.getName).filter(_.contains("社団"))
	def AB = (AM ++ PM).filter(_.contains("/"))
	def forName(name: String) = all.filter(_.getName == name).head
}

object CallArea {
	def cities = City.getAvailableCodes.asScala.map(new City(_)).filter(c=>c.getPrefName!=c.getCityName)
	def states = City.getAvailableCodes.asScala.map(new City(_)).filter(c=>c.getPrefName==c.getCityName)
	val area1 = List("東京都","神奈川県","埼玉県","千葉県","群馬県","茨城県","栃木県","山梨県")
	val inner = cities.filter(c =>  area1.contains(c.getPrefName)).map(_.getCityName)
	val outer = states.filter(c => !area1.contains(c.getPrefName)).map(_.getCityName)
	def total = inner ++ outer
}

object Sougou {
	def apply(record: Record)(implicit db: Database): Option[Record] = {
		val recordAM = Record.ofCall(record.call).filter(record => Sections.AM.contains(record.sect))
		val recordPM = Record.ofCall(record.call).filter(record => Sections.PM.contains(record.sect))
		if(recordAM.isEmpty || recordPM.isEmpty) return None
		if(!Sections.AB.contains(recordAM.last.sect)) return None
		if(!Sections.AB.contains(recordPM.last.sect)) return None
		if(recordAM.last.city != recordPM.last.city) return None
		val prefixAM = recordAM.last.sect.split(" ").take(3).toList
		val prefixPM = recordPM.last.sect.split(" ").take(3).toList
		if(prefixAM.take(2) != prefixPM.take(2)) return None
		val isPH = Seq(prefixAM.last,prefixPM.last).contains("電信電話")
		val mode = if(isPH) "電信電話" else "電信限定"
		return Some(recordAM.last.copy(
			sect = "%s %s %s 総合部門".format(prefixAM.take(2) :+ mode :_*),
			comm = "",
			file = null,
			calls = recordAM.last.calls + recordPM.last.calls,
			mults = recordAM.last.mults + recordPM.last.mults,
		))
	}
}

object Conflict {
	// delete conflicting entries
	def apply(scored: Scored)(implicit db: Database) {
		for(old <- Record.ofCall(scored.call) if(Sections.RC.contains(old.sect))) old.purge
		for(old <- Record.ofCall(scored.call) if(Sections.AM.contains(old.sect))) if(!Sections.PM.contains(scored.sect)) old.purge
		for(old <- Record.ofCall(scored.call) if(Sections.PM.contains(old.sect))) if(!Sections.AM.contains(scored.sect)) old.purge
		for(old <- Record.ofCall(scored.call) if(old.sect.contains("総合部門"))) old.purge
	}
}
