package models

import fxlog.field.Mode

object Modes {
	val options = List("電信", "電信電話")

	val cwMode = new Mode("電信")
	val phMode = new Mode("電話")

	def classify(mode: Mode) = mode.getValue.toUpperCase match {
		case "RTTY" => cwMode
		case "CW" => cwMode
		case _ => phMode
	}
}
