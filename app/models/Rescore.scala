package models

import java.io.FileInputStream

import fxlog.serial.LogSheetManager

object Rescore {
	val eLogFormat = new LogSheetManager().getFormat("fxlog")
	def recalc(entries: Seq[Entry]) = entries.foreach(entry => {
		val doc = eLogFormat.decode(new FileInputStream(entry.eLogFile))
		val mod = entry.prof.entry(doc)
		Storage.updateScore(entry, mod.calls, mod.mults)
		mod.save
	})
}
