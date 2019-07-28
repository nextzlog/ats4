package models

import java.nio.charset.Charset
import java.nio.file.{Files, Paths}
import java.util.{Timer, TimerTask}
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import play.api.{Configuration, Environment}
import scala.collection.JavaConverters._
import views.txt.pages.excel

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
	def total = excel().body.linesIterator.filter(_.nonEmpty)
	def store = Files.write(Report.file, total.toList.asJava)
	new Timer(true).schedule(new TimerTask {
		override def run() = store
	}, 0, 3600000)
}
