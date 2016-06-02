package models

import scala.collection.mutable.MutableList
import java.util.Date

class Joint(e1: Entry, e2: Entry) extends Entry(
	Prof(
		call = e1.call.getValue,
		city = e1.city.getValue,
		mobi = e1.mobi,
		band = "個人 総合",
		mode = e1.mode,
		name = e1.name,
		addr = e1.addr,
		mail = e1.mail,
		comm = None
	),
	time = if(e1.time after e2.time) e1.time else e2.time,
	calls = e1.calls + e2.calls,
	mults = e1.mults + e2.mults
)

object Joint {
	def jointable(lband: Entry, hband: Entry) = {
		lband.call == hband.call &&
		lband.city == hband.city &&
		lband.mode == hband.mode &&
		lband.mobi == hband.mobi &&
		Bands.forName(lband.band) == Bands.bandLB &&
		Bands.forName(hband.band) == Bands.bandHB
	}
	def all(): List[Entry] = {
		val all = Storage.all
		val lbands = Bands.inner(all, Bands.bandLB)
		val hbands = Bands.inner(all, Bands.bandHB)
		val joints = new MutableList[Entry]
		(lbands.keySet & hbands.keySet).foreach(call=>{
			val lbe = lbands(call)
			val hbe = hbands(call)
			if(jointable(lbe, hbe)) joints += new Joint(lbe, hbe)
		})
		return joints.toList
	}
}
