package models

import java.util.UUID

import qxsl.draft.Call

import play.api.data.{Form, Forms, OptionalMapping}

object TicketForm extends Form[Ticket] (
	Forms.mapping (
		"sect" -> Forms.text,
		"city" -> Forms.text
	)(Ticket.apply)(Ticket.unapply).verifying(p => Rule.absent(p.sect) || p.city.nonEmpty), Map.empty, Nil, None
)

object PersonForm extends Form[Person] (
	Forms.mapping (
		"call" -> Forms.nonEmptyText.verifying(Call.isValid(_)).transform(new Call(_).value(), identity[String]),
		"name" -> Forms.nonEmptyText,
		"post" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"note" -> Forms.text,
		"uuid" -> OptionalMapping(Forms.uuid).transform(_.getOrElse(Person.uuid), Some[UUID](_))
	)(Person.apply)(Person.unapply), Map.empty, Nil, None
)

object ClientForm extends Form[Client] (
	Forms.mapping (
		"person" -> PersonForm.mapping,
		"ticket" -> Forms.seq(TicketForm.mapping)
	)(Client.apply)(Client.unapply), Map.empty, Nil, None
)
