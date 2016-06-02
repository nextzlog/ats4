package models

import scala.collection.mutable.HashMap

import fxlog.field._

case class Bands(name: String, bands: Band *)

object Bands {
	val band19 = Bands("個人 1.9MHz帯", new Band(1900))
	val band35 = Bands("個人 3.5MHz帯", new Band(3500))
	val band70 = Bands("個人 7.0MHz帯", new Band(7000))
	val band14 = Bands("個人 14MHz帯" , new Band(14000))
	val band21 = Bands("個人 21MHz帯" , new Band(21000))
	val band28 = Bands("個人 28MHz帯" , new Band(28000))
	val band50 = Bands("個人 50MHz帯" , new Band(50000))
	val bandLB = Bands("個人 ローバンド", band19.bands ++ band35.bands ++ band70.bands: _*)
	val bandHB = Bands("個人 ハイバンド", band14.bands ++ band21.bands ++ band28.bands ++ band50.bands: _*)
	val bandAB = Bands("団体 全周波数帯", bandLB.bands ++ bandHB.bands: _*)

	val options = List(band19, band35, band70, band14, band21, band28, band50, bandLB, bandHB, bandAB).map(_.name)
	val forName = Map(
		band19.name -> band19,
		band35.name -> band35,
		band70.name -> band70,
		band14.name -> band14,
		band21.name -> band21,
		band28.name -> band28,
		band50.name -> band50,
		bandLB.name -> bandLB,
		bandHB.name -> bandHB,
		bandAB.name -> bandAB
	)

	val forBand = Map(
		band19.bands.head -> band19,
		band35.bands.head -> band35,
		band70.bands.head -> band70,
		band14.bands.head -> band14,
		band21.bands.head -> band21,
		band28.bands.head -> band28,
		band50.bands.head -> band50
	)

	def filter(entries: Seq[Entry], bands: Bands*): Map[Call, Entry] = {
		val map = new HashMap[Call, Entry]
		entries.filter(e => bands.map(_.name).contains(e.band)).foreach(e => map(e.call) = e)
		return map.toMap
	}

	def inner(entries: Seq[Entry], bands: Bands): Map[Call, Entry] = filter(entries, inner(bands.bands: _*): _*)
	def outer(entries: Seq[Entry], bands: Bands): Map[Call, Entry] = filter(entries, outer(bands.bands: _*): _*)

	def inner(bandSet: Band*): Seq[Bands] = forName.values.filter(bands => bands.bands.forall(bandSet.contains)).toSeq
	def outer(bandSet: Band*): Seq[Bands] = forName.values.filter(bands => bandSet.forall(bands.bands.contains)).toSeq
}
