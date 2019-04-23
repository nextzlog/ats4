package models

import play.api.{Configuration, Logger}
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.MailerClient

class Flow(implicit smtp: MailerClient, cfg: Configuration, db: Database) {
	def apply(user: User, temp: TemporaryFile): Post = {
		val path = temp.moveFileTo(user.file)
		val file = path.getFileName.toString
		val summ = QSOs(path.toString).summ(user)
		val post = user.post.copy(file=file, cnt=summ.calls, mul=summ.mults)
		Dupe.delete(post)(db)
		Logger.info(s"accept: $post")
		val postAllBands = HiLo(post)
		postAllBands.foreach(_.insert)
		new Mail().sendAcceptMail(post,postAllBands)
		post.insert
	}
}
