/*******************************************************************************
 * Amateur Radio Contest Administration System 'ATS-4' since 2017 April 2nd
 * License: GNU General Public License v3.0 (see LICENSE)
 * Author: Journal of Hamradio Informatics (https://pafelog.net)
*******************************************************************************/
package tasks

import java.io.{UncheckedIOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.util.{UUID, NoSuchElementException => Omiss}

import qxsl.model.Item
import qxsl.ruler._
import qxsl.sheet.{SheetManager, SheetOrTable}
import qxsl.table.TableManager

import ats4.data._
import ats4.root.ATS

import scala.jdk.CollectionConverters._

import play.api.{Logger, Configuration => Cfg}
import play.api.data.FormBinding
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.filters.csrf.CSRF
import play.libs.mailer.{Email, MailerClient}
import play.twirl.api.Html

import models._
import views.html.{pages, warns}

import akka.actor.{Actor, ActorRef}
import org.apache.commons.mail.EmailException

/**
 * 書類提出のリクエストを受け取り、得点計算と登録とメール返信までの処理を実行します。
 *
 *
 * @param smtp メールクライアントの依存性注入
 * @param ats データベースの依存性注入
 * @param rule コンテスト規約の依存性注入
 * @param admin 管理者権限
 */
class UploadTask(implicit smtp: MailerClient, ats: ATS, contest: Contest, admin: Boolean = false) {
	/**
	 * 書類提出のリクエストを処理して、確認画面のページのビューを返します。
	 *
	 *
	 * @param req リクエスト
	 * @param fb
	 * @return 確認画面または再提出のページ
	 */
	def accept(implicit req: Request[AnyContent], fb: FormBinding): Html = {
		val form = new ContestForm().bindFromRequest()
		if (CSRF.getToken.isDefined) util.Try {
			val post = form.get
			val files = req.body.asMultipartFormData.get.files
			val prevLogs = ats.archives().byCall(post.station.call)
			val stayLogs = prevLogs.asScala.map(f => f.file -> f).toMap
			ats.drop(post.station.call)
			val station = new StationData()
			station.call = post.station.call
			station.name = post.station.name
			station.post = post.station.post
			station.addr = post.station.addr
			station.mail = post.station.mail
			station.note = post.station.note
			station.uuid = post.station.uuid.toString
			ats.stations().push(station)
			val logs = collection.mutable.Buffer[Item]()
			for (up <- post.uploads if up.keep) util.Try {
				val file = stayLogs(up.file)
				logs.addAll(file.toItemList.asScala)
				ats.archives().push(file)
			}
			for (file <- files) util.Try {
				val archive = new ArchiveData()
				archive.call = post.station.call
				archive.file = file.filename
				archive.data = Files.readAllBytes(file.ref.path)
				ats.archives().push(archive)
				logs.addAll(archive.toItemList.asScala)
			}
			for (sect <- post.entries) {
				val rule = contest.section(sect.sect)
				val data = rule.summarize(logs.asJava)
				val ranking = new RankingData()
				ranking.call = post.station.call
				ranking.sect = sect.sect
				ranking.city = sect.city
				ranking.score = data.score()
				ranking.total = data.total()
				ats.rankings().push(ranking)
			}
			new SendMailFirstTask().send(station)
			Logger(this.getClass).info(s"accept: $post")
			pages.proof(post.station.call)
		}.recover {
			case ex: Omiss => pages.entry(form, Some(warns.omiss()))
		}.get else warns.token()
	}
}


/**
 * 書類削除のリクエストを受け取り、データベースから参加局を削除します。
 *
 *
 * @param req リクエストヘッダ
 * @param ats データベースの依存性注入
 * @param rule コンテスト規約の依存性注入
 */
class DeleteTask(implicit req: RequestHeader, ats: ATS, contest: Contest) {
	/**
	 * 指定された呼出符号の参加局を削除します。
	 *
	 * @param call 削除対象の呼出符号
	 * @return 管理画面のページ
	 */
	def delete(call: String): Html = util.Try {
		ats.drop(call)
		Logger(getClass).info(s"deleted: $call")
		pages.lists()
	}.getOrElse(pages.lists())

	/**
	 * 全ての参加局を削除します。
	 *
	 * @return 管理画面のページ
	 */
	def clear = {
		ats.deleteTables()
		ats.createTables()
		pages.lists()
	}
}


/**
 * アップロードされた交信記録がこの書類受付システムで処理可能か確認します。
 *
 *
 * @param req 交信記録を含むリクエスト
 */
class VerifyTask(implicit req: Request[AnyContent]) {
	/**
	 * 交信記録を読み取るデコーダです。
	 */
	val tables = new SheetOrTable()

	/**
	 * 交信記録を処理可能か確認して結果のメッセージを返します。
	 *
	 * @return 確認した結果を表すメッセージ
	 */
	def accept = util.Try {
		val files = req.body.asMultipartFormData.get.files.map(_.ref)
		files.foreach(f => tables.unpack(Files.readAllBytes(f)))
		warns.ready()
	}.recover {
		case ex: Chset => warns.chset()
		case ex: Unsup => warns.unsup()
	}.get
}


/**
 * アップロードされたJARLサマリーシートからフォームの初期値を抽出します。
 *
 *
 * @param req サマリーシートを含むリクエスト
 */
class FillInTask(implicit req: Request[AnyContent]) {
	/**
	 * サマリーシートを読み取るデコーダです。
	 */
	val sheets = new SheetManager().factory("jarl")

	/**
	 * サマリーシートからフォームの初期値を抽出してJSONデータを返します。
	 *
	 * @return JSONの文字列
	 */
	def accept = util.Try {
		val file = req.body.asMultipartFormData.get.files.map(_.ref).head
		val data = sheets.decoder(Files.readAllBytes(file)).decode()
		new SheetDecoderToJson(data).json
	}.getOrElse("")
}


/**
 * 参加局が提出した交信記録のファイルを返信します。
 *
 *
 * @param call 参加局の呼出符号
 * @param file 照会するファイルの名前
 * @param ats データベースの依存性注入
 */
class FileDLTask(call: String, file: String)(implicit ats: ATS) {
	/**
	 * 交信記録のファイルの内容を返します。
	 *
	 * @return 交信記録のバイト列
	 */
	def get = ats.archives().byCall(call).asScala.find(_.file == file).get.data
}


/**
 * 書類提出を受理した内容のメールを参加局に送信します。
 *
 *
 * @param smtp メールクライアントの依存性注入
 * @param ats データベースの依存性注入
 * @param contest コンテスト規約の依存性注入
 */
class SendMailFirstTask(implicit smtp: MailerClient, ats: ATS, contest: Contest) {
	/**
	 * 指定された参加局に対してメールを送信します。
	 *
	 * @param station 参加局の登録情報
	 */
	def send(station: StationData): Unit = util.Try {
		val mail = new Email
		val text = views.txt.pages.email(station.call).body.trim
		mail.setFrom("%s <%s>".format(contest.host, contest.mail))
		mail.addTo("%s <%s>".format(station.call, station.mail))
		mail.addBcc(contest.mail)
		mail.setSubject(text.linesIterator.toSeq.head.split(";").head.trim)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		smtp.send(mail)
	}.recover {
		case ex: EmailException => Logger("mail").error("MAIL ERROR!", ex)
	}
}


/**
 * 書類提出を受理した内容のメールを参加局に再送します。
 *
 *
 * @param smtp メールクライアントの依存性注入
 * @param ats データベースの依存性注入
 * @param contest コンテスト規約の依存性注入
 */
class SendMailAgainTask(implicit smtp: MailerClient, ats: ATS, contest: Contest) {
	/**
	 * 指定された参加局に対してメールを送信します。
	 *
	 * @param call 参加局の呼出符号
	 * @return 呼出符号
	 */
	def send(call: String): String = {
		ats.stations().byCall(call).asScala.foreach(new SendMailFirstTask().send)
		call
	}
}


/**
 * リアルタイムコンテストのストリーミング接続を確立して、交信記録の受信と得点状況の配信を実行します。
 *
 *
 * @param out
 * @param token 参加局を識別するトークン
 * @param cfg アプリケーションの設定の依存性注入
 * @param ats データベースの依存性注入
 * @param rule コンテスト規約の依存性注入
 */
class SocketTask(out: ActorRef, token: UUID)(implicit cfg: Cfg, ats: ATS, contest: Contest) extends Actor {
	/**
	 * 交信記録を読み取るデコーダです。
	 */
	val decoder = new SheetOrTable()

	/**
	 * クライアント側からのメッセージを処理します。
	 *
	 * @return 部分関数
	 */
	override def receive = {
		case msg: Array[Byte] => out ! push(msg).getBytes(UTF_8)
	}

	/**
	 * クライアント側からのメッセージを処理して、得点状況のJSONデータを生成します。
	 *
	 *
	 * @param data クライアント側からのメッセージ
	 * @return 得点状況をを格納したJSONの文字列
	 */
	def push(data: Array[Byte]): String = {
		val station = ats.stations().byUUID(token).get(0)
		val archive = ats.archives().byCall(station.call).asScala
		val ranking = ats.rankings().byCall(station.call).asScala
		val qsoList = archive.map(_.toItemList.asScala).flatten
		val qsoDiff = decoder.unpack(data.tail).asScala
		qsoList --= qsoDiff.take(data.head.toInt & 0xFF)
		qsoList ++= qsoDiff.drop(data.head.toInt & 0xFF)
		archive.head.data = new TableManager().encode(qsoList.asJava)
		ats.archives().byCall(station.call).asScala.foreach(ats.archives().drop)
		ats.rankings().byCall(station.call).asScala.foreach(ats.rankings().drop)
		val sums = ranking.map(_.rule(contest).summarize(qsoList.asJava))
		ranking.lazyZip(sums).map(_.copy(_)).foreach(ats.rankings().push)
		ats.archives().push(archive.head)
		Logger(this.getClass).info(s"accept: $station.call")
		new RankingTableToJson().json
	}
}
