package models

import play.api.Play.current
import play.api.Play.{configuration => conf}

import java.io._

object Awards {
	val file = new File(Entry.saveDir, "awards.csv")
	def output() {
		val out = new PrintStream(file, "UTF8")
		Section.all.foreach(sect => sect.awards.foreach(entry => {
			out.print(s"${entry.call},")
			out.print(s"${entry.city},")
			out.print(s"${entry.sect},")
			out.print(s"${sect.rank(entry.score) + 1}位,")
			out.print(s"${entry.name},")
			out.print(s"${entry.addr},")
			out.print(s"${entry.mail}")
			out.println
		}))
		out.close
	}
}
