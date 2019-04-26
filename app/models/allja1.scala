package models

import play.api.db.Database
import qxsl.field.City
import qxsl.ruler.Contest
import scala.collection.JavaConverters._

object Sections {
	val all = Contest.forName("allja1.lisp").asScala.toList
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
	def sect(u: User) = "1エリア%s %s".format(if(inner.contains(u.city)) "内" else "外",u.sect)
	val inner = cities.filter(c =>  area1.contains(c.getPrefName)).map(_.getCityName)
	val outer = states.filter(c => !area1.contains(c.getPrefName)).map(_.getCityName)
	def total = inner ++ outer
}

object AllBands {
	def apply(post: Post)(implicit db: Database): Option[Post] = {
		val postAM = Post.ofCall(post.call).filter(post => Sections.AM.contains(post.sect))
		val postPM = Post.ofCall(post.call).filter(post => Sections.PM.contains(post.sect))
		if(postAM.nonEmpty && postPM.nonEmpty) {
			val amIsAB = Sections.AB.contains(postAM.last.sect)
			val pmIsAB = Sections.AB.contains(postPM.last.sect)
			if(amIsAB && pmIsAB) {
				if(postAM.last.city == postPM.last.city) {
					val prefixAM = postAM.last.sect.split(" ").take(3).toList
					val prefixPM = postPM.last.sect.split(" ").take(3).toList
					if(prefixAM.take(2) == prefixPM.take(2)) {
						val mode = (prefixAM.last, prefixPM.last) match {
							case ("電信電話",_) => "電信電話"
							case (_,"電信電話") => "電信電話"
							case _ => "電信限定"
						}
						val postAB = postAM.last.copy(
							sect = "%s %s 総合部門".format(prefixAM.take(2).mkString(" "), mode),
							cnt = postAM.last.cnt + postPM.last.cnt,
							mul = postAM.last.mul + postPM.last.mul,
							comm = ""
						)
						return Some(postAB)
					}
				}
			}
		}
		return None
	}
}

object Conflict {
	// delete conflicting entries
	def apply(post: Post)(implicit db: Database) {
		for(old <- Post.ofCall(post.call) if(Sections.RC.contains(old.sect))) old.delete
		for(old <- Post.ofCall(post.call) if(Sections.AM.contains(old.sect))) if(!Sections.PM.contains(post.sect)) old.delete
		for(old <- Post.ofCall(post.call) if(Sections.PM.contains(old.sect))) if(!Sections.AM.contains(post.sect)) old.delete
		for(old <- Post.ofCall(post.call) if(old.sect.contains("総合部門"))) old.delete
	}
}
