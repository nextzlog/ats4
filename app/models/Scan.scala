package models

import java.io.File
import qxsl.ruler.Summary
import qxsl.sheet.Sheets
import qxsl.table.Tables
import scala.util.Try

case class Scan(elog: String) {
	val sheets = new Sheets()
	val tables = new Tables()
	val source = Elog(elog).toURI.toURL
	val sheet = Try(sheets.decode(source).get("LOGSHEET").getBytes)
	val table = Try(tables.decode(sheet.get)).getOrElse(tables.decode(source))
	def summ(prof: Prof) = new Summary(table, Conf.sect(prof.fullSect))
	def summ(post: Post) = new Summary(table, Conf.sect(post.sect))
}
