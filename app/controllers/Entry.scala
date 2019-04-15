package controllers

import java.io.{ByteArrayInputStream, File, FileInputStream, IOException}
import java.text.SimpleDateFormat
import javax.inject.Inject
import play.Logger
import play.api.db.Database
import play.api.mvc.{Action, InjectedController}
import play.libs.mailer.{Email, MailerClient}
import qxsl.ruler.Summary
import qxsl.sheet.Sheets
import qxsl.table.Tables
import scala.collection.JavaConversions._

import views._
import models._

class Entry @Inject() (smtp: MailerClient)(implicit db: Database) extends InjectedController {
	def date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date)
	if(!Conf.save.isDirectory) Conf.save.mkdirs
	def submit = Action(implicit req => if(Conf.isOK) Ok(html.submit(Prof.form)) else BadRequest(html.index()))
	def accept = Action(parse.multipartFormData) (implicit req => {
		val rcvd = Prof.form.bindFromRequest
		rcvd.fold (
			errs => BadRequest(html.submit(rcvd, "未入力の項目があります")),
			prof => req.body.file("eLog") match {
				case None => BadRequest(html.submit(rcvd, "更新記録ファイルを添付してください"))
				case Some(file) => try {
					val elog = new File(Conf.save, s"${prof.safeCall}.${date}.log")
					file.ref.moveTo(elog)
					Logger.info(s"saved: ${elog}")
					val table = (new Tables).decode(try {
						new ByteArrayInputStream((new Sheets).decode(new FileInputStream(elog))("LOGSHEET").getBytes)
					} catch {
						case ex: Exception => new FileInputStream(elog)
					})
					val summ = new Summary(table, Conf.sect(prof.fullSect))
					val post = prof.post(summ)
					Logger.info(s"accept: ${post}")
					Dupe.delete(post)
					post.insert
					sendAcceptMail(post)
					// automatic entry into "sougou" category
					val postAllBands = HiLo.post(post)
					postAllBands.foreach(_.insert)
					postAllBands.foreach(sendAcceptMail)
					Ok(html.accept(post, summ, date))
				} catch {
					case ex: Exception => Logger.error(s"failed to store ${prof}", ex)
					BadRequest(html.submit(rcvd, "提出できません。主催者にお問い合わせください"))
				}
			}
		)
	})
	def sendAcceptMail(post: Post) = for(to <- Post.ofCall(post.call).map(_.mail).distinct) {
		val mail = new Email
		mail.setSubject(Conf.subj)
		mail.setFrom("%s <%s>".format(Conf.host, Conf.repl))
		mail.addTo("%s <%s>".format(post.call, to))
		mail.addBcc(Conf.repl)
		smtp.send(mail.setBodyText(views.txt.mail(post).body.trim))
	}
}
