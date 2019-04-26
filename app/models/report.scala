package models

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import java.util.{Timer, TimerTask}
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import play.api.{Configuration, Environment}
import scala.collection.JavaConverters._

class Module extends play.api.inject.Module {
	def bindings(env: Environment, conf: Configuration) = {
		Seq(bind[Report].toSelf.eagerly)
	}
}

@Singleton class Report @Inject()(implicit db: Database) {
	new Timer(true).schedule(new TimerTask {
		override def run() {
			val cset = Charset.forName("SJIS")
			val path = Paths.get("ats4.rcvd", "report.csv")
			val list = views.txt.pages.excel(db).body.lines.toSeq
			Files.write(path, list.filter(_.nonEmpty).asJava,cset)
		}
	}, 0, 3600000)
}
