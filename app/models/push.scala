package models

import java.nio.file.Paths
import play.api.Configuration
import play.api.Logger
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.libs.mailer.{Email, MailerClient}

class Scoring(implicit cfg: Configuration, db: Database, smtp: MailerClient) {
	def push(post: Post, temp: TemporaryFile): Team = {
		val file = Storage.file(post.team.call).toString
		temp.moveFileTo(Paths.get(file).toFile)
		val games = post.games(file)
		Book.del(post.team)
		Book.add(post.team)
		games.foreach(Book.del)
		games.foreach(Book.add)
		Logger(post.getClass).info(s"accepted: $post")
		val mail = new Email
		val text = views.txt.pages.email(post.team).body.trim
		mail.setFrom("%s <%s>".format(Contest.host, Contest.mail))
		mail.addTo("%s <%s>".format(post.team.call, post.team.mail))
		mail.addBcc(Contest.mail)
		mail.setSubject(text.linesIterator.toSeq.head.split(";").head.trim)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		util.Try(smtp.send(mail))
		Book.dump
		post.team
	}
}
