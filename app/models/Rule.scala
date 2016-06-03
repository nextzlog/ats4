package models

import java.util.HashSet
import qxsl.model._

import models.Filter._

import scala.collection.JavaConversions._

case class Rule(id: Int, filters: Record=>Boolean *) {
	def validate(rec: Record) = filters.forall(_(rec))
	def pickout(doc: Document) = doc.getRecords.filter(validate)
	def cutdown(doc: Document) = doc.getRecords.filterNot(validate)
}

object Rule {
	val forName = Map(
		"1エリア内電信個人 1.9MHz帯"   -> Rule(1000, TimeTable.isOK, 運用場所, 電信限定, band19),
		"1エリア内電信個人 3.5MHz帯"   -> Rule(1001, TimeTable.isOK, 運用場所, 電信限定, band35),
		"1エリア内電信個人 7.0MHz帯"   -> Rule(1002, TimeTable.isOK, 運用場所, 電信限定, band70),
		"1エリア内電信個人 14MHz帯"    -> Rule(1003, TimeTable.isOK, 運用場所, 電信限定, band14),
		"1エリア内電信個人 21MHz帯"    -> Rule(1004, TimeTable.isOK, 運用場所, 電信限定, band21),
		"1エリア内電信個人 28MHz帯"    -> Rule(1005, TimeTable.isOK, 運用場所, 電信限定, band28),
		"1エリア内電信個人 50MHz帯"    -> Rule(1006, TimeTable.isOK, 運用場所, 電信限定, band50),
		"1エリア内電信個人 ローバンド" -> Rule(1010, TimeTable.isOK, 運用場所, 電信限定, ローバンド),
		"1エリア内電信個人 ハイバンド" -> Rule(1011, TimeTable.isOK, 運用場所, 電信限定, ハイバンド),
		"1エリア内電信個人 総合"       -> Rule(1012, TimeTable.isOK, 運用場所, 電信限定, 全周波数帯),
		"1エリア内電信団体 全周波数帯" -> Rule(1020, TimeTable.isOK, 運用場所, 電信限定, 全周波数帯),

		"1エリア内電信電話個人 1.9MHz帯"   -> Rule(1100, TimeTable.isOK, 運用場所, band19),
		"1エリア内電信電話個人 3.5MHz帯"   -> Rule(1101, TimeTable.isOK, 運用場所, band35),
		"1エリア内電信電話個人 7.0MHz帯"   -> Rule(1102, TimeTable.isOK, 運用場所, band70),
		"1エリア内電信電話個人 14MHz帯"    -> Rule(1103, TimeTable.isOK, 運用場所, band14),
		"1エリア内電信電話個人 21MHz帯"    -> Rule(1104, TimeTable.isOK, 運用場所, band21),
		"1エリア内電信電話個人 28MHz帯"    -> Rule(1105, TimeTable.isOK, 運用場所, band28),
		"1エリア内電信電話個人 50MHz帯"    -> Rule(1106, TimeTable.isOK, 運用場所, band50),
		"1エリア内電信電話個人 ローバンド" -> Rule(1110, TimeTable.isOK, 運用場所, ローバンド),
		"1エリア内電信電話個人 ハイバンド" -> Rule(1111, TimeTable.isOK, 運用場所, ハイバンド),
		"1エリア内電信電話個人 総合"       -> Rule(1112, TimeTable.isOK, 運用場所, 全周波数帯),
		"1エリア内電信電話団体 全周波数帯" -> Rule(1120, TimeTable.isOK, 運用場所, 全周波数帯),

		"1エリア外電信個人 1.9MHz帯"   -> Rule(2000, TimeTable.isOK, 運用場所, エリア外, 電信限定, band19),
		"1エリア外電信個人 3.5MHz帯"   -> Rule(2001, TimeTable.isOK, 運用場所, エリア外, 電信限定, band35),
		"1エリア外電信個人 7.0MHz帯"   -> Rule(2002, TimeTable.isOK, 運用場所, エリア外, 電信限定, band70),
		"1エリア外電信個人 14MHz帯"    -> Rule(2003, TimeTable.isOK, 運用場所, エリア外, 電信限定, band14),
		"1エリア外電信個人 21MHz帯"    -> Rule(2004, TimeTable.isOK, 運用場所, エリア外, 電信限定, band21),
		"1エリア外電信個人 28MHz帯"    -> Rule(2005, TimeTable.isOK, 運用場所, エリア外, 電信限定, band28),
		"1エリア外電信個人 50MHz帯"    -> Rule(2006, TimeTable.isOK, 運用場所, エリア外, 電信限定, band50),
		"1エリア外電信個人 ローバンド" -> Rule(2010, TimeTable.isOK, 運用場所, エリア外, 電信限定, ローバンド),
		"1エリア外電信個人 ハイバンド" -> Rule(2011, TimeTable.isOK, 運用場所, エリア外, 電信限定, ハイバンド),
		"1エリア外電信個人 総合"       -> Rule(2012, TimeTable.isOK, 運用場所, エリア外, 電信限定, 全周波数帯),
		"1エリア外電信団体 全周波数帯" -> Rule(2020, TimeTable.isOK, 運用場所, エリア外, 電信限定, 全周波数帯),

		"1エリア外電信電話個人 1.9MHz帯"   -> Rule(2100, TimeTable.isOK, 運用場所, エリア外, band19),
		"1エリア外電信電話個人 3.5MHz帯"   -> Rule(2101, TimeTable.isOK, 運用場所, エリア外, band35),
		"1エリア外電信電話個人 7.0MHz帯"   -> Rule(2102, TimeTable.isOK, 運用場所, エリア外, band70),
		"1エリア外電信電話個人 14MHz帯"    -> Rule(2103, TimeTable.isOK, 運用場所, エリア外, band14),
		"1エリア外電信電話個人 21MHz帯"    -> Rule(2104, TimeTable.isOK, 運用場所, エリア外, band21),
		"1エリア外電信電話個人 28MHz帯"    -> Rule(2105, TimeTable.isOK, 運用場所, エリア外, band28),
		"1エリア外電信電話個人 50MHz帯"    -> Rule(2106, TimeTable.isOK, 運用場所, エリア外, band50),
		"1エリア外電信電話個人 ローバンド" -> Rule(2110, TimeTable.isOK, 運用場所, エリア外, ローバンド),
		"1エリア外電信電話個人 ハイバンド" -> Rule(2111, TimeTable.isOK, 運用場所, エリア外, ハイバンド),
		"1エリア外電信電話個人 総合"       -> Rule(2112, TimeTable.isOK, 運用場所, エリア外, 全周波数帯),
		"1エリア外電信電話団体 全周波数帯" -> Rule(2120, TimeTable.isOK, 運用場所, エリア外, 全周波数帯)
	)
}
