package models

import java.text.Normalizer.{normalize, Form}
import scala.collection.JavaConversions._

case class Prof(call: String, city: String, sect: String, name: String, addr: String, mail: String, comm: String) {
	def safeCall = normalize(call.toUpperCase, Form.NFKC).split('/').head.replaceAll("[^0-9A-Z]", "") // prohibit directory traversal
	def post(summ: qxsl.ruler.Summary) = Post(call, city, sect, name, addr, mail, comm, summ.calls, summ.mults)
}
