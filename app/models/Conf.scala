package models

import java.io.File
import java.time.LocalDate
import qxsl.ruler.Contest
import scala.collection.JavaConverters._

object Conf {
	val demo = true
	def isOK = LocalDate.now().getMonthValue() == 6 || demo
	val test = Contest.forName("allja1.lisp")
	def sects = test.asScala.toList
	def sectsAM = sects.map(_.getName).filter(_.matches(""".*(1\.9|3\.5|7).*"""))
	def sectsPM = sects.map(_.getName).filter(_.matches(""".*(14|21|28|50).*"""))
	def sectsRC = sects.map(_.getName).filter(_.contains("社団"))
	def sectsAllBands = (sectsAM ++ sectsPM).filter(_.contains("/"))
	def sect(name: String) = sects.filter(_.getName == name).head
}
