package models

import fxlog.field.City

case class Pref(name: String)

object Pref {
	def area(pref: Pref): Int = pref match {
		case Pref("北海道")   => 8
		case Pref("青森県")   => 7
		case Pref("岩手県")   => 7
		case Pref("秋田県")   => 7
		case Pref("山形県")   => 7
		case Pref("宮城県")   => 7
		case Pref("福島県")   => 7
		case Pref("新潟県")   => 0
		case Pref("長野県")   => 0
		case Pref("東京都")   => 1
		case Pref("神奈川県") => 1
		case Pref("千葉県")   => 1
		case Pref("埼玉県")   => 1
		case Pref("茨城県")   => 1
		case Pref("栃木県")   => 1
		case Pref("群馬県")   => 1
		case Pref("山梨県")   => 1
		case Pref("静岡県")   => 2
		case Pref("岐阜県")   => 2
		case Pref("愛知県")   => 2
		case Pref("三重県")   => 2
		case Pref("京都府")   => 3
		case Pref("滋賀県")   => 3
		case Pref("奈良県")   => 3
		case Pref("大阪府")   => 3
		case Pref("和歌山県") => 3
		case Pref("兵庫県")   => 3
		case Pref("富山県")   => 9
		case Pref("福井県")   => 9
		case Pref("石川県")   => 9
		case Pref("岡山県")   => 4
		case Pref("島根県")   => 4
		case Pref("山口県")   => 4
		case Pref("鳥取県")   => 4
		case Pref("広島県")   => 4
		case Pref("香川県")   => 5
		case Pref("徳島県")   => 5
		case Pref("愛媛県")   => 5
		case Pref("高知県")   => 5
		case Pref("福岡県")   => 6
		case Pref("佐賀県")   => 6
		case Pref("長崎県")   => 6
		case Pref("熊本県")   => 6
		case Pref("大分県")   => 6
		case Pref("宮崎県")   => 6
		case Pref("鹿児島県") => 6
		case Pref("沖縄県")   => 6
		case _ => throw new IllegalArgumentException(String.valueOf(pref))
	}
	def area(city: City): Int = area(Place.numToPref(Place.cityToNum(city)))
}
