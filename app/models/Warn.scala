package models

sealed trait Warn {
	def toString(): String
}

object Omiss extends Warn {
	override def toString() = "記入に漏れがあるか、交信記録が未添付です"
}

object Unsup extends Warn {
	override def toString() = "交信記録の書式が崩れているか、未対応です"
}
