package models

import java.util.HashSet
import fxlog.field._
import fxlog.model._

object Count {
	def count(records: Record*) = {
		val calls = new HashSet[(Band, Mode, Call)]
		val mults = new HashSet[(Band, City)]
		records.foreach(rec => {
			val band = rec.getBand
			val mode = rec.getMode
			if(calls.add((band, mode, rec.getCall))) {
				mults.add((band, rec.getRcvdCity))
			}
		})
		(calls.size, mults.size)
	}
}
