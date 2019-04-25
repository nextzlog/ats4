package models

import java.time.LocalDate
import play.api.Configuration

object Gate {
	def demo(implicit cfg: Configuration) = cfg.get[Boolean]("ats4.demo")
	def isOK(implicit cfg: Configuration) = LocalDate.now().getMonthValue() == 6 || demo
}
