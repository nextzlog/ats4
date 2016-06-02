package models

import play.api.Play.current
import play.api.Play.{configuration => conf}

import java.io._

import models.Place._

object DatFile {
	val name = conf.getString("ats3.datname").getOrElse("zlog.dat")
	val file = {
		val tmp = File.createTempFile("ats3dat", "zlog")
		tmp.deleteOnExit
		var len = 0
		cityNames.foreach(city => len = Math.max(len, city.length))
		val fmt = "%%-%ds %%%%s\r\n".format(len)
		val fos = new FileOutputStream(tmp)
		val out = new PrintStream(fos, true, "SJIS")
		out.printf("%s for ZLOGCG.EXE\r\n", name)
		cities.foreach(c => out.printf(fmt.format(cityToNum(c)), c))
		out.printf("end of file %s", name)
		out.flush
		out.close
		tmp
	}
}
