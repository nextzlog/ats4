package models

import play.api.db.Database

case class Sect(name: String)(implicit db: Database) {
	val sorted = Post.ofSect(name).sortBy(_.score).reverse
	val winners = sorted.size
	def place(s: Post) = sorted.indexWhere(_.score == s.score)
	def prize(s: Post) = place(s) <= Math.min(winners / 10, 6)
}

object Sect {
	def all()(implicit db: Database) = Conf.sects.map(s=>Sect(s.getName))
}
