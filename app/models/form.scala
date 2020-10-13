package models

import scala.util.Try
import play.api.data.{Form, Forms}
import qxsl.draft.Call

case class Part(sect: String, city: String)
case class Info(call: String, name: String, addr: String, mail: String, comm: String)
case class Post(info: Info, list: Seq[Option[Part]]) {
	def call = new Call(info.call).value()
	def take = list.filter(_.isDefined).map(_.get)
}

object Part {
	def fill(call: String) = {
		val recs = Record.findAllByCall(call)
		Subtests.groups.map(g => recs.find(_.code == g._1).map(_.toPart))
	}
}

object Info {
	def fill(call: String) = Person.findAllByCall(call).head.toInfo
}

object Post {
	def fill(call: String) = Post(info = Info.fill(call), list = Part.fill(call))
	def form(call: String) = Try(PostForm.fill(Post.fill(call))).getOrElse(PostForm)
}

object PartForm extends Form[Option[Part]] (
	Forms.optional(
		Forms.mapping (
			"sect" -> Forms.text,
			"city" -> Forms.text
		)(Part.apply)(Part.unapply).verifying(p => p.sect.nonEmpty && p.city.nonEmpty)
	), Map.empty, Nil, None
)

object InfoForm extends Form[Info] (
	Forms.mapping (
		"call" -> Forms.nonEmptyText.verifying(Call.isValid(_)),
		"name" -> Forms.nonEmptyText,
		"addr" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"comm" -> Forms.text
	)(Info.apply)(Info.unapply), Map.empty, Nil, None
)

object PostForm extends Form[Post] (
	Forms.mapping (
		"info" -> InfoForm.mapping,
		"list" -> Forms.seq(PartForm.mapping)
	)(Post.apply)(Post.unapply), Map.empty, Nil, None
)
