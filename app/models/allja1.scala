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
	def apply(call: String)(implicit db: Database): Option[Record] = {
		val records = Record.ofCall(call).filter(r=>Sections.AB.contains(r.sect))
		if(!records.exists(r=>Sections.AM.contains(r.sect))) return None
		if(!records.exists(r=>Sections.PM.contains(r.sect))) return None
		if(records.map(_.city).distinct.size != 1) return None
		val isPH = records.exists(_.sect.contains("電信電話"))
		val club = records.head.sect.split(" ")(1)
		val part = "%s 電信%s 総合部門".format(club, if(isPH) "電話" else "限定")
		val sucs = records.map(r=>Tables(r.file,r.sect).score.accepted.asScala)
		val logs = sucs.flatten.map(_.item)
		val scan = records.head.scaned.copy(part=part, comm="")
		return scan.next(Sections.forName(scan.sect).summarize(logs.asJava)).next
	}
}

object Disposal {
	// delete conflicting entries
	def apply(scored: Scored)(implicit db: Database) {
		for(old <- Record.ofCall(scored.call) if(Sections.RC.contains(old.sect))) old.purge
		for(old <- Record.ofCall(scored.call) if(Sections.AM.contains(old.sect))) if(!Sections.PM.contains(scored.sect)) old.purge
		for(old <- Record.ofCall(scored.call) if(Sections.PM.contains(old.sect))) if(!Sections.AM.contains(scored.sect)) old.purge
		for(old <- Record.ofCall(scored.call) if(old.sect.contains("総合部門"))) old.purge
	}
}
