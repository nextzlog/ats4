package models

import java.io.File

object Elog {
	def apply(elog: String) = new File(Conf.save, elog)
}
