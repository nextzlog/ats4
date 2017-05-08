package models

import java.io.File
import org.joda.time.DateTime
import qxsl.ruler.Contest
import scala.collection.JavaConversions._

object Conf {
	val name = "ALLJA1"
	val what = "ALLJA1コンテストは関東地方を中心としたアマチュア無線の全国大会です"
	val host = "東京大学アマチュア無線クラブ"
	val mail = "allja1@ja1zlo.u-tokyo.org"
	val site = "ja1zlo.u-tokyo.org"
	val rule = "ja1zlo.u-tokyo.org/allja1/28rule.html"
	val ats3 = "allja1.org"
	val subj = "ALLJA1 Entry Acceptance"
	val repl = "allja1@ja1zlo.u-tokyo.org"
	def isOK = new DateTime().getMonthOfYear() == 6
	def save = new File(System.getProperty("user.home"), "ats4.rcvd")
	val test = Contest.forName("allja1.lisp")
	def sects = test.toList
	def sectsAM = sects.map(_.getName).filter(_.matches(""".*(1\.9|3\.5|7).*"""))
	def sectsPM = sects.map(_.getName).filter(_.matches(""".*(14|21|28|50).*"""))
	def sectsRC = sects.map(_.getName).filter(_.contains("社団"))
	def sectsAllBands = (sectsAM ++ sectsPM).filterNot(_.contains("/"))
	def sect(name: String) = sects.filter(_.getName == name).head
}
