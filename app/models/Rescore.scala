package models

import java.io.FileInputStream

import qxsl.sheet.LogSheetFormat

object Rescore {
	val eLogFormat = LogSheetFormat.forName("qxml")
	def recalc(entries: Seq[Entry]) = entries.foreach(entry => {
		val doc = eLogFormat.decode(new FileInputStream(entry.eLogFile))
		val mod = entry.prof.entry(doc)
		Storage.updateScore(entry, mod.calls, mod.mults)
		mod.save
	})
}
