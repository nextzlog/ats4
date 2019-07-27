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

object Report {
	val path = Files.createDirectories(Paths.get("ats4.rcvd"))
	val file = path.resolve("report.csv")
}

@Singleton class Report @Inject()(implicit db: Database) {
	def csv = views.txt.pages.excel(db).body.lines.iterator.asScala.filter(_.nonEmpty)
	new Timer(true).schedule(new TimerTask {
		override def run() = Files.write(Report.file, csv.toSeq.asJava)
	}, 0, 3600000)
}
