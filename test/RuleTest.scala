package test

import org.specs2.mutable._
import org.specs2.specification.BeforeExample

import play.api.test._
import play.api.test.Helpers._

import scala.{Boolean => B}

import fxlog.field._
import fxlog.model._

import models._

class RuleTest extends Specification with BeforeExample {
	val OK = true
	val no = false
	var testCases1 : List[(Record, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B)] = null
	var testCases_ : List[(Record, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B, B)] = null
	def fakeApp = FakeApplication()

	def before = running(fakeApp) {
		val timeL = new Time(TimeTable.lowBandStart)
		val timeH = new Time(TimeTable.highBandStart)
		def record(time: Time, band: Int, mode: String, city: String): Record = {
			val rec = new Record()
			rec.setTime(time)
			rec.setBand(new Band(band))
			rec.setMode(new Mode(mode))
			rec.setRcvdCity(new City(city))
			return rec
		}
		testCases1 = List(
			//                                        CW only                                   CW and Phone
			//                                        1.9 3.5 7.0 14  21  28  50  LOW HI  ALL   1.9 3.5 7.0 14  21  28  50 LOW  HI  ALL
			(record(timeL, 1900,  "電信", "宗谷")   , OK, no, no, no, no, no, no, OK, no, OK,   OK, no, no, no, no, no, no, OK, no, OK),
			(record(timeL, 1900,  "電信", "長野県") , OK, no, no, no, no, no, no, OK, no, OK,   OK, no, no, no, no, no, no, OK, no, OK), 
			(record(timeL, 1900,  "電信", "調布市") , OK, no, no, no, no, no, no, OK, no, OK,   OK, no, no, no, no, no, no, OK, no, OK),

			(record(timeL, 3500,  "電信", "宗谷")   , no, OK, no, no, no, no, no, OK, no, OK,   no, OK, no, no, no, no, no, OK, no, OK),
			(record(timeL, 3500,  "電信", "長野県") , no, OK, no, no, no, no, no, OK, no, OK,   no, OK, no, no, no, no, no, OK, no, OK), 
			(record(timeL, 3500,  "電信", "調布市") , no, OK, no, no, no, no, no, OK, no, OK,   no, OK, no, no, no, no, no, OK, no, OK),
			(record(timeL, 3500,  "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, OK, no, no, no, no, no, OK, no, OK),
			(record(timeL, 3500,  "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, OK, no, no, no, no, no, OK, no, OK), 
			(record(timeL, 3500,  "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, OK, no, no, no, no, no, OK, no, OK),

			(record(timeL, 7000,  "電信", "宗谷")   , no, no, OK, no, no, no, no, OK, no, OK,   no, no, OK, no, no, no, no, OK, no, OK),
			(record(timeL, 7000,  "電信", "長野県") , no, no, OK, no, no, no, no, OK, no, OK,   no, no, OK, no, no, no, no, OK, no, OK), 
			(record(timeL, 7000,  "電信", "調布市") , no, no, OK, no, no, no, no, OK, no, OK,   no, no, OK, no, no, no, no, OK, no, OK),
			(record(timeL, 7000,  "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, OK, no, no, no, no, OK, no, OK),
			(record(timeL, 7000,  "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, OK, no, no, no, no, OK, no, OK), 
			(record(timeL, 7000,  "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, OK, no, no, no, no, OK, no, OK),

			(record(timeH, 14000, "電信", "宗谷")   , no, no, no, OK, no, no, no, no, OK, OK,   no, no, no, OK, no, no, no, no, OK, OK),
			(record(timeH, 14000, "電信", "長野県") , no, no, no, OK, no, no, no, no, OK, OK,   no, no, no, OK, no, no, no, no, OK, OK), 
			(record(timeH, 14000, "電信", "調布市") , no, no, no, OK, no, no, no, no, OK, OK,   no, no, no, OK, no, no, no, no, OK, OK),
			(record(timeH, 14000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, OK, no, no, no, no, OK, OK),
			(record(timeH, 14000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, OK, no, no, no, no, OK, OK), 
			(record(timeH, 14000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, OK, no, no, no, no, OK, OK),

			(record(timeH, 21000, "電信", "宗谷")   , no, no, no, no, OK, no, no, no, OK, OK,   no, no, no, no, OK, no, no, no, OK, OK),
			(record(timeH, 21000, "電信", "長野県") , no, no, no, no, OK, no, no, no, OK, OK,   no, no, no, no, OK, no, no, no, OK, OK), 
			(record(timeH, 21000, "電信", "調布市") , no, no, no, no, OK, no, no, no, OK, OK,   no, no, no, no, OK, no, no, no, OK, OK),
			(record(timeH, 21000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, OK, no, no, no, OK, OK),
			(record(timeH, 21000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, OK, no, no, no, OK, OK), 
			(record(timeH, 21000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, OK, no, no, no, OK, OK),

			(record(timeH, 28000, "電信", "宗谷")   , no, no, no, no, no, OK, no, no, OK, OK,   no, no, no, no, no, OK, no, no, OK, OK),
			(record(timeH, 28000, "電信", "長野県") , no, no, no, no, no, OK, no, no, OK, OK,   no, no, no, no, no, OK, no, no, OK, OK), 
			(record(timeH, 28000, "電信", "調布市") , no, no, no, no, no, OK, no, no, OK, OK,   no, no, no, no, no, OK, no, no, OK, OK),
			(record(timeH, 28000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, OK, no, no, OK, OK),
			(record(timeH, 28000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, OK, no, no, OK, OK), 
			(record(timeH, 28000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, OK, no, no, OK, OK),

			(record(timeH, 50000, "電信", "宗谷")   , no, no, no, no, no, no, OK, no, OK, OK,   no, no, no, no, no, no, OK, no, OK, OK),
			(record(timeH, 50000, "電信", "長野県") , no, no, no, no, no, no, OK, no, OK, OK,   no, no, no, no, no, no, OK, no, OK, OK), 
			(record(timeH, 50000, "電信", "調布市") , no, no, no, no, no, no, OK, no, OK, OK,   no, no, no, no, no, no, OK, no, OK, OK),
			(record(timeH, 50000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, OK, no, OK, OK),
			(record(timeH, 50000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, OK, no, OK, OK), 
			(record(timeH, 50000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, OK, no, OK, OK)
		)
		testCases_ = List(
			(record(timeL, 1900,  "電信", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeL, 1900,  "電信", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeL, 1900,  "電信", "調布市") , OK, no, no, no, no, no, no, OK, no, OK,   OK, no, no, no, no, no, no, OK, no, OK),

			(record(timeL, 3500,  "電信", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeL, 3500,  "電信", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeL, 3500,  "電信", "調布市") , no, OK, no, no, no, no, no, OK, no, OK,   no, OK, no, no, no, no, no, OK, no, OK),
			(record(timeL, 3500,  "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeL, 3500,  "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeL, 3500,  "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, OK, no, no, no, no, no, OK, no, OK),

			(record(timeL, 7000,  "電信", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeL, 7000,  "電信", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeL, 7000,  "電信", "調布市") , no, no, OK, no, no, no, no, OK, no, OK,   no, no, OK, no, no, no, no, OK, no, OK),
			(record(timeL, 7000,  "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeL, 7000,  "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeL, 7000,  "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, OK, no, no, no, no, OK, no, OK),

			(record(timeH, 14000, "電信", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 14000, "電信", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 14000, "電信", "調布市") , no, no, no, OK, no, no, no, no, OK, OK,   no, no, no, OK, no, no, no, no, OK, OK),
			(record(timeH, 14000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 14000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 14000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, OK, no, no, no, no, OK, OK),

			(record(timeH, 21000, "電信", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 21000, "電信", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 21000, "電信", "調布市") , no, no, no, no, OK, no, no, no, OK, OK,   no, no, no, no, OK, no, no, no, OK, OK),
			(record(timeH, 21000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 21000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 21000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, OK, no, no, no, OK, OK),

			(record(timeH, 28000, "電信", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 28000, "電信", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 28000, "電信", "調布市") , no, no, no, no, no, OK, no, no, OK, OK,   no, no, no, no, no, OK, no, no, OK, OK),
			(record(timeH, 28000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 28000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 28000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, OK, no, no, OK, OK),

			(record(timeH, 50000, "電信", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 50000, "電信", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 50000, "電信", "調布市") , no, no, no, no, no, no, OK, no, OK, OK,   no, no, no, no, no, no, OK, no, OK, OK),
			(record(timeH, 50000, "電話", "宗谷")   , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no),
			(record(timeH, 50000, "電話", "長野県") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, no, no, no, no), 
			(record(timeH, 50000, "電話", "調布市") , no, no, no, no, no, no, no, no, no, no,   no, no, no, no, no, no, OK, no, OK, OK)
		)
	}

	"1エリア内部門" should {
		"1.9MHz帯電信部門"       in testCases1.forall(e => Rule.forName("1エリア内電信個人 1.9MHz帯")      .validate(e._1) == e._2)
		"3.5MHz帯電信部門"       in testCases1.forall(e => Rule.forName("1エリア内電信個人 3.5MHz帯")      .validate(e._1) == e._3)
		"7.0MHz帯電信部門"       in testCases1.forall(e => Rule.forName("1エリア内電信個人 7.0MHz帯")      .validate(e._1) == e._4)
		"14MHz帯電信部門"        in testCases1.forall(e => Rule.forName("1エリア内電信個人 14MHz帯")       .validate(e._1) == e._5)
		"21MHz帯電信部門"        in testCases1.forall(e => Rule.forName("1エリア内電信個人 21MHz帯")       .validate(e._1) == e._6)
		"28MHz帯電信部門"        in testCases1.forall(e => Rule.forName("1エリア内電信個人 28MHz帯")       .validate(e._1) == e._7)
		"50MHz帯電信部門"        in testCases1.forall(e => Rule.forName("1エリア内電信個人 50MHz帯")       .validate(e._1) == e._8)
		"ローバンド電信部門"     in testCases1.forall(e => Rule.forName("1エリア内電信個人 ローバンド")    .validate(e._1) == e._9)
		"ハイバンド電信部門"     in testCases1.forall(e => Rule.forName("1エリア内電信個人 ハイバンド")    .validate(e._1) == e._10)
		"全周波数帯電信部門"     in testCases1.forall(e => Rule.forName("1エリア内電信団体 全周波数帯")    .validate(e._1) == e._11)		

		"1.9MHz帯電信電話部門"   in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 1.9MHz帯")  .validate(e._1) == e._12)
		"3.5MHz帯電信電話部門"   in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 3.5MHz帯")  .validate(e._1) == e._13)
		"7.0MHz帯電信電話部門"   in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 7.0MHz帯")  .validate(e._1) == e._14)
		"14MHz帯電信電話部門"    in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 14MHz帯")   .validate(e._1) == e._15)
		"21MHz帯電信電話部門"    in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 21MHz帯")   .validate(e._1) == e._16)
		"28MHz帯電信電話部門"    in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 28MHz帯")   .validate(e._1) == e._17)
		"50MHz帯電信電話部門"    in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 50MHz帯")   .validate(e._1) == e._18)
		"ローバンド電信電話部門" in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 ローバンド").validate(e._1) == e._19)
		"ハイバンド電信電話部門" in testCases1.forall(e => Rule.forName("1エリア内電信電話個人 ハイバンド").validate(e._1) == e._20)
		"全周波数帯電信電話部門" in testCases1.forall(e => Rule.forName("1エリア内電信電話団体 全周波数帯").validate(e._1) == e._21)
	}

	"1エリア外部門" should {
		"1.9MHz帯電信部門"       in testCases_.forall(e => Rule.forName("1エリア外電信個人 1.9MHz帯")      .validate(e._1) == e._2)
		"3.5MHz帯電信部門"       in testCases_.forall(e => Rule.forName("1エリア外電信個人 3.5MHz帯")      .validate(e._1) == e._3)
		"7.0MHz帯電信部門"       in testCases_.forall(e => Rule.forName("1エリア外電信個人 7.0MHz帯")      .validate(e._1) == e._4)
		"14MHz帯電信部門"        in testCases_.forall(e => Rule.forName("1エリア外電信個人 14MHz帯")       .validate(e._1) == e._5)
		"21MHz帯電信部門"        in testCases_.forall(e => Rule.forName("1エリア外電信個人 21MHz帯")       .validate(e._1) == e._6)
		"28MHz帯電信部門"        in testCases_.forall(e => Rule.forName("1エリア外電信個人 28MHz帯")       .validate(e._1) == e._7)
		"50MHz帯電信部門"        in testCases_.forall(e => Rule.forName("1エリア外電信個人 50MHz帯")       .validate(e._1) == e._8)
		"ローバンド電信部門"     in testCases_.forall(e => Rule.forName("1エリア外電信個人 ローバンド")    .validate(e._1) == e._9)
		"ハイバンド電信部門"     in testCases_.forall(e => Rule.forName("1エリア外電信個人 ハイバンド")    .validate(e._1) == e._10)
		"全周波数帯電信部門"     in testCases_.forall(e => Rule.forName("1エリア外電信団体 全周波数帯")    .validate(e._1) == e._11)		

		"1.9MHz帯電信電話部門"   in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 1.9MHz帯")  .validate(e._1) == e._12)
		"3.5MHz帯電信電話部門"   in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 3.5MHz帯")  .validate(e._1) == e._13)
		"7.0MHz帯電信電話部門"   in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 7.0MHz帯")  .validate(e._1) == e._14)
		"14MHz帯電信電話部門"    in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 14MHz帯")   .validate(e._1) == e._15)
		"21MHz帯電信電話部門"    in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 21MHz帯")   .validate(e._1) == e._16)
		"28MHz帯電信電話部門"    in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 28MHz帯")   .validate(e._1) == e._17)
		"50MHz帯電信電話部門"    in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 50MHz帯")   .validate(e._1) == e._18)
		"ローバンド電信電話部門" in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 ローバンド").validate(e._1) == e._19)
		"ハイバンド電信電話部門" in testCases_.forall(e => Rule.forName("1エリア外電信電話個人 ハイバンド").validate(e._1) == e._20)
		"全周波数帯電信電話部門" in testCases_.forall(e => Rule.forName("1エリア外電信電話団体 全周波数帯").validate(e._1) == e._21)
	}
}
