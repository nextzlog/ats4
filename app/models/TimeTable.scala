package models

import play.api.Play.current
import play.api.Play.{configuration => conf}

import java.text.SimpleDateFormat
import java.util.Calendar
import qxsl.field._
import qxsl.model._

object TimeTable {
	val contestdate   = conf.getString("ats3.contestdate").get
	val dateFormat    = new SimpleDateFormat("yyyy/MM/dd")
	val contestDate   = dateFormat.parse(contestdate)
	val timeFormat    = new SimpleDateFormat("yyyy/MM/dd HH:mm")
	val highBandStart = timeFormat.parse(contestdate + " 09:00")
	val highBandEnd   = timeFormat.parse(contestdate + " 12:00")
	val lowBandStart  = timeFormat.parse(contestdate + " 16:00")
	val lowBandEnd    = timeFormat.parse(contestdate + " 20:00")
	val bandTimeTable = List(
		(Bands.band19,  lowBandStart,  lowBandEnd),
		(Bands.band35,  lowBandStart,  lowBandEnd),
		(Bands.band70,  lowBandStart,  lowBandEnd),
		(Bands.band14, highBandStart, highBandEnd),
		(Bands.band21, highBandStart, highBandEnd),
		(Bands.band28, highBandStart, highBandEnd),
		(Bands.band50, highBandStart, highBandEnd)
	)
	def isOK(rec: Record) = (rec.getTime.getValue, bandTimeTable.find {
		case(band, _, _) => band.bands.contains(rec.getBand)
		case _ => false
	}) match {
		case(time, Some((_, start, end))) => !(start.after(time) || end.before(time))
		case(_, None) => false
	}
}
