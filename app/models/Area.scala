package models

import qxsl.field.City
import scala.collection.JavaConverters._

object Area {
	def cities = City.getAvailableCodes.asScala.map(new City(_)).filter(c=>c.getPrefName!=c.getCityName)
	def states = City.getAvailableCodes.asScala.map(new City(_)).filter(c=>c.getPrefName==c.getCityName)
	val area1 = List("東京都","神奈川県","埼玉県","千葉県","群馬県","茨城県","栃木県","山梨県")
	def sect(u: User) = "1エリア%s %s".format(if(inner.contains(u.city)) "内" else "外",u.sect)
	val inner = cities.filter(c =>  area1.contains(c.getPrefName)).map(_.getCityName)
	val outer = states.filter(c => !area1.contains(c.getPrefName)).map(_.getCityName)
	def total = inner ++ outer
}
