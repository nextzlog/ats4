package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.validation.Constraints._

import views._
import models._

import qxsl.sheet._

import java.io._
import java.sql.SQLException

import org.apache.commons.mail.EmailException

object Submit extends Controller {
	val eLogFormat = LogSheetFormat.forName("qxml")
	val profileForm: Form[Prof] = Form(
		mapping(
			"call" -> nonEmptyText,
			"city" -> nonEmptyText,
			"mobi" -> nonEmptyText,
			"band" -> nonEmptyText,
			"mode" -> nonEmptyText,
			"name" -> nonEmptyText,
			"addr" -> nonEmptyText,
			"mail" -> email,
			"comm" -> optional(text)
		)(Prof.apply)(Prof.unapply)
	)

	def form = Action(if(TimeLimit.isOK) Ok(html.form(profileForm)) else BadRequest(html.index()))

	def summary = Action(parse.multipartFormData) {implicit req =>
		val rcvd = profileForm.bindFromRequest
		def error(messages: String*) = BadRequest(html.form(rcvd, messages: _*))
		rcvd.fold(errs => error("未入力の項目があります"), prof => req.body.file("eLog") match {
			case Some(file) => try {
				val doc = Decoder.decode(file.ref.file)
				val ent = prof.entry(doc)
				Storage.insert(ent)
				ent.save
				eLogFormat.encode(new FileOutputStream(ent.eLogFile), doc)
				Mail.accept(ent)
				Ok(html.summary(ent, doc))
			} catch {
				case ex: SQLException => {
					Logger.error("cannot insert into DB " + prof, ex)
					error("入力が長過ぎます")
				}
				case ex: FileNotFoundException => {
					Logger.error("cannot I/O " + Entry.saveDir, ex)
					error("管理者に連絡してください")
				}
				case ex: IOException => {
					Logger.error("undecodable " + file.filename + " from " + prof, ex)
					error("未対応の交信記録フォーマットです")
				}
				case ex: Exception => {
					Logger.error("cannot send mail to " + prof, ex)
					error("管理者に連絡してください")
				}
			}
			case None => error("交信記録ファイルを選択してください")
		})
	}
}
