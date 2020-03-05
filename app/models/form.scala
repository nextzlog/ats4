package models

import java.nio.file.{Files, Path, Paths}
import play.api.data.{Form, Forms}
import play.api.libs.Files.TemporaryFile
import play.api.{Configuration, Logger}
import play.libs.mailer.{Email, MailerClient}
import qxsl.ruler.{Contest, RuleKit, Section}
import scala.util.Try

case class Part(sect: String, city: String = "") {
	def code = rule.getCode
	def rule = Sections.find(sect)
	def game(call: String, file: String) = {
		val score = new TableLoader(file, sect=sect).score
		Game(call, sect, city, score=score.score, total=score.total, file=file)
	}
}

case class Post(team: Team, parts: Seq[Option[Part]]) {
	def games(file: String)(implicit cfg: Configuration): Seq[Game] = {
		val parts = this.parts.filter(_.nonEmpty).map(_.get)
		val name = parts.map(_.rule)
		val sect = Part(Sections.joint(name).getName)
		return (parts:+sect).map(_.game(team.call, file))
	}
}

object PostForm extends Form[Post](Forms.mapping(
	"team" -> Forms.mapping(
		"call" -> Forms.nonEmptyText,
		"name" -> Forms.nonEmptyText,
		"addr" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"comm" -> Forms.text,
	)(Team.apply)(Team.unapply),
	"parts" -> Forms.seq(Forms.optional(Forms.mapping(
		"sect" -> Forms.text,
		"city" -> Forms.text
	)(Part.apply)(Part.unapply).verifying(Sections.valid _)))
)(Post.apply)(Post.unapply), Map.empty, Nil, None)
