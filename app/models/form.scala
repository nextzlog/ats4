package models

import java.util.UUID

import qxsl.draft.Call

import play.api.data.{Form, Forms, OptionalMapping}

object SectionForm extends Form[SectionData](
	Forms.mapping(
		"sect" -> Forms.text,
		"city" -> Forms.text
	)(SectionData.apply)(SectionData.unapply).verifying(p => Rule.absent(p.sect) || p.city.nonEmpty), Map.empty, Nil, None
)

object StationForm extends Form[StationData](
	Forms.mapping(
		"call" -> Forms.nonEmptyText.verifying(Call.isValid(_)).transform(new Call(_).value(), identity[String]),
		"name" -> Forms.nonEmptyText,
		"post" -> Forms.nonEmptyText,
		"addr" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"note" -> Forms.text,
		"uuid" -> OptionalMapping(Forms.uuid).transform(_.getOrElse(StationData.uuid), Some[UUID](_))
	)(StationData.apply)(StationData.unapply), Map.empty, Nil, None
)

object ContestForm extends Form[ContestData](
	Forms.mapping(
		"station" -> StationForm.mapping,
		"section" -> Forms.seq(SectionForm.mapping)
	)(ContestData.apply)(ContestData.unapply), Map.empty, Nil, None
)
