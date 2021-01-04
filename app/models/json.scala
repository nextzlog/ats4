package models

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax._

import scala.jdk.CollectionConverters._

case class RecordJson(call: String, score: Int, total: Int)

object RecordJson {
	implicit val encoder: Encoder[RecordJson] = deriveEncoder
	def latest = Rule.rule.asScala.map(s => s.name() -> {
		Record.findAllBySect(s.name()).map(_.json)
	}).toMap.asJson.spaces2SortKeys
}
