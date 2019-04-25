package models

import java.nio.file.Files
import java.util.{Timer, TimerTask}
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.collection.JavaConverters._

@Singleton class Init @Inject()(implicit db: Database) {
	class FinishTask extends TimerTask {
		override def run() {
			val path = QSOs.save.resolve("report.csv")
			val list = views.txt.pages.excel(db).body.lines
			Files.write(path, list.filter(_.nonEmpty).toSeq.asJava)
		}
	}
	new Timer(true).schedule(new FinishTask, 0, 3600000)
}
