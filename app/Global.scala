import play.api._

import java.util._

import models._

object Global extends GlobalSettings {
	override def onStart(app: Application) {
		Rescore.recalc(Storage.all)
		class FinishTask extends TimerTask {
			override def run() {
				Awards.output
			}
		}
		val timer = new Timer(true)
		val delay = TimeLimit.deadline
		timer.schedule(new FinishTask, delay)
	}
}
