package models

import qxsl.field.Band
import qxsl.field.Freq

case class Freqs(band: Band, minFreq: Int, maxFreq: Int)

object Freqs {
	val list = List(
		Freqs(new Band(1900),  1810,  1913),
		Freqs(new Band(3500),  3500,  3687),
		Freqs(new Band(7000),  7000,  7200),
		Freqs(new Band(14000), 14000, 14350),
		Freqs(new Band(21000), 21000, 21450),
		Freqs(new Band(28000), 28000, 29700),
		Freqs(new Band(50000), 50000, 54000)
	)

	def band(freq: Freq) = list.find(range => range.minFreq <= freq.getValue && freq.getValue <= range.maxFreq) match {
		case Some(Freqs(band, minFreq, maxFreq)) => band
		case None => null
	}
}
