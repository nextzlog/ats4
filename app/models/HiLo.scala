package models

import play.api.db.Database

object HiLo {
	def apply(post: Post)(implicit db: Database): Option[Post] = {
		val postAM = Post.ofCall(post.call).filter(post => Test.sectsAM.contains(post.sect))
		val postPM = Post.ofCall(post.call).filter(post => Test.sectsPM.contains(post.sect))
		if(postAM.nonEmpty && postPM.nonEmpty) {
			val amIsAB = Test.sectsAB.contains(postAM.last.sect)
			val pmIsAB = Test.sectsAB.contains(postPM.last.sect)
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
