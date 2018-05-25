package models

import java.text.Normalizer.{normalize, Form}
import scala.collection.JavaConversions._

case class Prof(call: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String) {
	def dispCall = normalize(call.toUpperCase, Form.NFKC)
	def safeCall = dispCall.split('/').head.replaceAll("[^0-9A-Z]", "") // prohibit directory traversal
	def fullSect = "1エリア%s %s".format(if(Conf.inner.contains(qxsl.field.City.forName(city))) "内" else "外", sect)
	def post(summ: qxsl.ruler.Summary) = Post(safeCall, dispCall, city, fullSect, name, addr, mail, comm, summ.calls, summ.mults)
}
