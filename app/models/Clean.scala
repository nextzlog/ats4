package models

import java.util.NoSuchElementException

import qxsl.field._
import qxsl.model._

import scala.collection.JavaConversions._

object Clean {
	def cleanAll(doc: Document) = doc.getRecords.map(clean)
	def clean(rec: Record): Record = {
		val band = rec.getBand
		val freq = rec.getFreq
		val mode = Modes.classify(rec.getMode)
		val rcvd = (rec.getRcvdRST, rec.getRcvdSerial) match {
			case (null, num) => mode match {
				case Modes.cwMode => num.getValue.substring(3).trim
				case Modes.phMode => num.getValue.substring(2).trim
			}
			case (_, num) => num.getValue
		}
		val cleand = new Record()
		cleand.setTime(rec.getTime)
		cleand.setCall(new Call(rec.getCall.getValue.split("/", 2)(0)))
		cleand.setBand(if(freq != null) Freqs.band(freq) else band)
		cleand.setMode(mode)
		cleand.setRcvdCity(Place.numToCity.getOrElse(rcvd, null))
		cleand.setRcvdSerial(new Serial(rcvd))
		return cleand
	}
}
