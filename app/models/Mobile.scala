package models

object Mobile {
	val options = List("固定局", "移動局")
	def isMobile(kind: String) = "移動局" == kind
}
