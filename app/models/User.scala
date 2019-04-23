package models

import java.text.Normalizer
import play.api.data.{Form, Forms}
import qxsl.ruler.Summary

case class User(call: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String) {
	def disp = Normalizer.normalize(call.toUpperCase, Normalizer.Form.NFKC)
	def safe = disp.split('/').head.replaceAll("[^0-9A-Z]", "")
	val file = QSOs.file(QSOs.name(safe)) // safety against directory traversal
	def post = Post(-1,call=safe,disp=disp,city=city,sect=Area.sect(this),name=name,addr=addr,mail=mail,comm=comm)
}

object User {
	val form = Form(Forms.mapping(
		"call" -> Forms.nonEmptyText,
		"city" -> Forms.nonEmptyText,
		"sect" -> Forms.nonEmptyText,
		"name" -> Forms.nonEmptyText,
		"addr" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"comm" -> Forms.text
	)(User.apply)(User.unapply))
}
