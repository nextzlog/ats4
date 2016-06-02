package models

import java.util.Date
import java.text.ParseException

import fxlog.field._
import fxlog.model._

case class Prof(
	call: String,
	city: String,
	mobi: String,
	band: String,
	mode: String,
	name: String,
	addr: String,
	mail: String,
	comm: Option[String]) {
	def sect: String = (Pref.area(new City(city)) match {
		case 1 => "1エリア内"
		case _ => "1エリア外"
	}) + mode + band
	def entry(doc: Document) = Count.count(Rule.forName(sect).pickout(doc): _*) match {
		case(calls, mults) => Entry(this, new Date(), calls, mults)
	}
}

object Prof {
	def toHalfWidth(str: String): String = str.map {
		case ch if 'ａ' <= ch && ch <= 'ｚ' => (ch - 'ａ' + 'a').toChar
		case ch if 'Ａ' <= ch && ch <= 'Ｚ' => (ch - 'Ａ' + 'A').toChar
		case ch if '０' <= ch && ch <= '９' => (ch - '０' + '0').toChar
		case ch => ch
	}
}
