package models

import play.api.{Configuration, Logger}
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.MailerClient

class Flow(implicit smtp: MailerClient, cfg: Configuration, db: Database) {
	def apply(prof: Prof, temp: TemporaryFile): Post = {
		temp.moveFileTo(Elog(prof.elog))
		val post = prof.post(Scan(prof.elog).summ(prof))
		Dupe.delete(post)(db)
		Logger.info(s"accept: $post")
		val postAllBands = HiLo(post)
		postAllBands.foreach(_.insert)
		new Mail().sendAcceptMail(post,postAllBands)
		post.insert
	}
}
