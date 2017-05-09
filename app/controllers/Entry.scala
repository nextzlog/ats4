package controllers

import java.io.{ByteArrayInputStream, File, FileInputStream, IOException}
import java.text.SimpleDateFormat
import javax.inject.Inject

import play.Logger
import play.api.Play.current
import play.api.Play.{configuration => conf}
import play.api.data.{Form, Forms}
import play.api.db.Database
import play.api.mvc.{Action, Controller}
import play.libs.mailer.{Email, MailerClient}

import qxsl.ruler.Summary
import qxsl.sheet.Sheets
import qxsl.table.Tables

import scala.collection.JavaConversions._

import views._
import models._

class Entry @Inject()(smtp: MailerClient)(implicit db: Database) extends Controller {
	def date = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date)
	val form = Form(Forms.mapping(
		"call" -> Forms.nonEmptyText,
		"city" -> Forms.nonEmptyText,
		"sect" -> Forms.nonEmptyText,
		"name" -> Forms.nonEmptyText,
		"addr" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"comm" -> Forms.text
	)(Prof.apply)(Prof.unapply))
	if(!Conf.save.isDirectory) Conf.save.mkdirs
	def submit = Action(if(Conf.isOK) Ok(html.submit(form)) else BadRequest(html.index()))
	def accept = Action(parse.multipartFormData) (implicit req => {
		val rcvd = form.bindFromRequest
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
					val summ = new Summary(table, Conf.sect(prof.sect))
					val post = prof.post(summ)
					Logger.info(s"accept: ${post}")
					// delete conflicting entries
					for(old <- Post.ofCall(prof.call) if(Conf.sectsRC.contains(old.sect))) old.delete
					// delete conflicting entries
					for(old <- Post.ofCall(prof.call) if(Conf.sectsAM.contains(old.sect))) {
						if(!Conf.sectsPM.contains(prof.sect)) old.delete
					}
					// delete conflicting entries
					for(old <- Post.ofCall(prof.call) if(Conf.sectsPM.contains(old.sect))) {
						if(!Conf.sectsAM.contains(prof.sect)) old.delete
					}
					post.insert
					sendAcceptMail(post)
					// automatic entry into "sougou" category
					val postAM = Post.ofCall(prof.call).filter(post => Conf.sectsAM.contains(post.sect))
					val postPM = Post.ofCall(prof.call).filter(post => Conf.sectsPM.contains(post.sect))
					if(postAM.nonEmpty && postPM.nonEmpty) {
						val amIsAllBands = Conf.sectsAllBands.contains(postAM.last)
						val pmIsAllBands = Conf.sectsAllBands.contains(postPM.last)
						if(amIsAllBands && pmIsAllBands) {
							if(postAM.last.city == postPM.last.city) {
								val prefixAM = postAM.last.sect.split(" ").take(3).mkString
								val prefixPM = postPM.last.sect.split(" ").take(3).mkString
								if(prefixAM == prefixPM) {
									val postAllBands = postAM.last.copy(
										sect = prefixAM + " 総合部門",
										cnt = postAM.last.cnt + postPM.last.cnt,
										mul = postAM.last.mul + postPM.last.mul,
										comm = ""
									)
									postAllBands.insert
									sendAcceptMail(postAllBands)
								}
							}
						}
					}
					Ok(html.accept(post, summ, date))
				} catch {
					case ex: Exception => Logger.error(s"failed to store ${prof}", ex)
					BadRequest(html.submit(rcvd, "提出できません。主催者にお問い合わせください"))
				}
			}
		)
	})
	def sendAcceptMail(post: Post) = for(to <- Post.ofCall(post.call).map(_.mail)) {
		val mail = new Email
		mail.setSubject(Conf.subj)
		mail.setFrom("%s <%s>".format(Conf.host, Conf.repl))
		mail.addTo("%s <%s>".format(post.call, to))
		smtp.send(mail.setBodyText(views.txt.mail(post).body.trim))
	}
}
