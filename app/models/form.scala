package models

import java.util.UUID

import qxsl.draft.Call

import play.api.data.{Form, Forms, OptionalMapping}

class TicketForm(test: String) extends Form[Ticket](
	Forms.mapping(
		"sect" -> Forms.text,
		"city" -> Forms.text
	)(Ticket.apply)(Ticket.unapply).verifying(p => Rule.absent(p.sect) || p.city.nonEmpty), Map.empty, Nil, None
)

class PersonForm(test: String) extends Form[Person](
	Forms.mapping(
		"test" -> Forms.ignored(test),
		"call" -> Forms.nonEmptyText.verifying(Call.isValid(_)).transform(new Call(_).value(), identity[String]),
		"name" -> Forms.nonEmptyText,
		"post" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"note" -> Forms.text,
		"uuid" -> OptionalMapping(Forms.uuid).transform(_.getOrElse(Person.uuid), Some[UUID](_))
	)(Person.apply)(Person.unapply), Map.empty, Nil, None
)

class ClientForm(test: String) extends Form[Client](
	Forms.mapping(
		"person" -> PersonForm(test).mapping,
		"ticket" -> Forms.seq(TicketForm(test).mapping)
	)(Client.apply)(Client.unapply), Map.empty, Nil, None
)

object TicketForm {
	def apply(test: String) = new TicketForm(test)
}

object PersonForm {
	def apply(test: String) = new PersonForm(test)
}

object ClientForm {
	def apply(test: String) = new ClientForm(test)
}
