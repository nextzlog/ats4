package models

import java.io.PrintStream
import java.util.{Timer, TimerTask}
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import models.{QSOs, Sect}

@Singleton class Init @Inject()(db: Database) {
	class FinishTask extends TimerTask {
		override def run() {
			val out = new PrintStream(QSOs.save.resolve("report.csv").toFile, "SJIS")
			out.print("呼出符号,")
			out.print("運用場所,")
			out.print("参加部門,")
			out.print("順位,")
			out.print("入賞,")
			out.print("得点,")
			out.print("名前,")
			out.print("住所,")
			out.print("アドレス,")
			out.print("コメント,")
			out.println
			for(sect <- Sect.all(db); post <- sect.sorted) {
				val rank = sect.place(post) + 1
				out.print(s"${post.call},")
				out.print(s"${post.city},")
				out.print(s"${post.sect},")
				out.print(if(rank < 2) "優勝," else s"${rank}位,")
				out.print(if(sect.prize(post)) "入賞局," else ",")
				out.print(s"${post.cnt * post.mul}点,")
				out.print(s"${post.name},")
				out.print(s"${post.addr},")
				out.print(s"${post.mail},")
				out.print(s"${post.comm},")
				out.println
			}
			out.close
		}
	}
	new Timer(true).schedule(new FinishTask, 0, 3600000)
}
