package models

import java.text.Normalizer.{normalize, Form}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import play.api.data.{Form => PlayForm, Forms}
import qxsl.field.City
import qxsl.ruler.Summary

case class Prof(call: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String) {
	def dispCall = normalize(call.toUpperCase, Form.NFKC)
	def safeCall = dispCall.split('/').head.replaceAll("[^0-9A-Z]", "") // prohibit directory traversal
	def fullSect = "1エリア%s %s".format(if(Conf.inner.contains(City.forName(city))) "内" else "外", sect)
	val elog = LocalDateTime.now.format(DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm.ss'.log'"))
	def post(s: Summary) = Post(-1, safeCall, dispCall, city, fullSect, name, addr, mail, comm, s.calls, s.mults, elog)
}

object Prof {
	val form = PlayForm(Forms.mapping(
		"call" -> Forms.nonEmptyText,
		"city" -> Forms.nonEmptyText,
		"sect" -> Forms.nonEmptyText,
		"name" -> Forms.nonEmptyText,
		"addr" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"comm" -> Forms.text
	)(Prof.apply)(Prof.unapply))
}
