package models

import qxsl.sheet.SheetDecoder

import scala.jdk.CollectionConverters._

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import io.circe.syntax._

case class RankingJson(call: String, score: Int, total: Int)

object RankingJson {
	implicit val encoder: Encoder[RankingJson] = deriveEncoder
	def latest = Rule.rule.asScala.map(s => s.name() -> RankingData.findAllBySect(s.name()).map(_.json)).toMap.asJson.spaces2SortKeys
}

object SummaryJson {
	def apply(d: SheetDecoder) = Map(
		"call" -> d.getString("CALLSIGN"),
		"name" -> d.getString("NAME"),
		"addr" -> d.getString("ADDRESS"),
		"mail" -> d.getString("EMAIL"),
		"note" -> d.getString("COMMENTS")
	).view.mapValues(Option.apply).toMap.asJson.spaces2SortKeys
}
