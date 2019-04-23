package models

import java.nio.file.{Files, Paths}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import qxsl.ruler.Summary
import qxsl.sheet.Sheets
import qxsl.table.Tables
import scala.util.Try

case class QSOs(path: String) {
	val sheets = new Sheets()
	val tables = new Tables()
	val source = Paths.get(path).toUri.toURL
	val sheet = Try(sheets.decode(source).get("LOGSHEET").getBytes)
	val table = Try(tables.decode(sheet.get)).getOrElse(tables.decode(source))
	def summ(user: User) = new Summary(table, Conf.sect(Area.sect(user)))
	def summ(post: Post) = new Summary(table, Conf.sect(post.sect))
}

object QSOs {
	val save = Paths.get(System.getProperty("user.dir")).resolve("rcvd")
	val dfmt = DateTimeFormatter.ofPattern("'%s'.yyyyMMdd.HHmmss.'log'")
	def file(name: String) = save.resolve(name).toFile
	def name(call: String) = LocalDateTime.now.format(dfmt).format(call)
	if(Files.notExists(save)) Files.createDirectories(save)
}
