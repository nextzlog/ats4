package models

import play.api.Play.current
import play.api.Play.{configuration => conf}

import scala.io.Source
import scala.collection.mutable.MutableList

import qxsl.field.City

case class Place(num: String, city: City, pref: Pref)

object Place {
	val list: List[Place] = {
		val places = new MutableList[Place]
		val source = Source.fromFile("conf/places.dat", "UTF-8")
		source.getLines.filter(_.nonEmpty).foreach(_.split(" +", 3) match {
			case Array(p, c, n) => places += Place(n, new City(c), new Pref(p))
		})
		source.close
		places.toList
	}
	def cities = list.map(_.city)
	val cityNames = list.map(_.city.getValue)
	val numToCity = list.map(place => (place.num -> place.city)).toMap
	val numToPref = list.map(place => (place.num -> place.pref)).toMap
	val cityToNum = list.map(place => (place.city -> place.num)).toMap
}
