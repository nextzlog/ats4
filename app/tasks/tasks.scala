/*******************************************************************************
 * Amateur Radio Contest Administration System 'ATS-4' since 2017 April 2nd
 * Released under the GNU General Public License (GPL) v3 (see LICENSE)
 * Univ. Tokyo Amateur Radio Club Development Task Force (nextzlog.dev)
*******************************************************************************/
package tasks

import java.io.{UncheckedIOException => Unsup}
import java.nio.charset.{CharacterCodingException => Chset}
import java.nio.charset.StandardCharsets.UTF_8
import java.nio.file.Files
import java.util.{UUID, NoSuchElementException => Omiss}

import qxsl.ruler._
import qxsl.sheet.{SheetManager, SheetOrTable}

import ats4.data._

import scala.jdk.CollectionConverters._

import play.api.Logger
import play.api.data.FormBinding
import play.api.mvc.{AnyContent, Request, RequestHeader}
import play.filters.csrf.CSRF
import play.libs.mailer.Email
import play.twirl.api.Html

import injects.Injections
import models._
import views.html.{pages, warns}

import akka.actor.{Actor, ActorRef}
import org.apache.commons.mail.EmailException

/**
 * 書類提出のリクエストを受け取り、得点計算と登録とメール返信までの処理を実行します。
 *
 *
 * @param in 依存性注入
 * @param admin 管理者権限
 */
class UploadTask(implicit in: Injections, admin: Boolean) {
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
			val station = new StationData()
			station.call = post.station.call
			station.name = post.station.name
			station.post = post.station.post
			station.addr = post.station.addr
			station.mail = post.station.mail
			station.note = post.station.note
			station.uuid = post.station.uuid.toString
			val prevs = in.ats.archives().byCall(station.call)
			val kepts = prevs.asScala.map(f => f.file -> f).toMap
			in.ats.drop(station.call)
			in.ats.stations().push(station)
			for (up <- post.uploads if up.keep) util.Try {
				in.ats.archives().push(kepts(up.file))
				in.ats.messages().push(kepts(up.file))
			}
			for (file <- files) util.Try {
				val archive = station.archive().load(file.ref.path)
				archive.file = file.filename
				in.ats.archives().push(archive)
				in.ats.messages().push(archive)
			}.recover {
				case ex => Logger(this.getClass).error(s"failed: $post", ex)
			}
			val list = post.marshal.map(MarshalFormData.encode).asJava
			if (!list.isEmpty) util.Try {
				val archive = station.archive().load(list)
				archive.file = ""
				in.ats.archives().push(archive)
				in.ats.messages().push(archive)
			}
			for (sect <- post.entries) {
				val ranking = station.ranking()
				ranking.sect = sect.sect
				ranking.city = sect.city
				in.ats.rankings().push(ranking)
			}
			in.ats.update(station.call, in.rule)
			Logger(this.getClass).info(s"accept: $post")
			new NotifyTask().send(station)
			pages.proof(post.station.call)
		}.recover {
			case ex: Omiss => pages.entry(form, Some(warns.omiss()))
		}.get else warns.token()
	}
}


/**
 * 登録済みの全ての参加局に対し、得点計算を再実行します。
 *
 *
 * @param req リクエストヘッダ
 * @param in 依存性注入
 * @param admin 管理者権限
 */
class UpdateTask(implicit req: RequestHeader, in: Injections, admin: Boolean) {
	/**
	 * 得点計算を再実行します。
	 *
	 * @return 管理画面のページ
	 */
	def accept: Html = {
		for (message <- in.ats.messages().list().asScala) util.Try(in.ats.messages().drop(message))
		for (archive <- in.ats.archives().list().asScala) util.Try(in.ats.messages().push(archive))
		for (station <- in.ats.stations().list().asScala) in.ats.update(station.call, in.rule)
		pages.lists()
	}
}


/**
 * 書類削除のリクエストを受け取り、データベースから参加局を削除します。
 *
 *
 * @param req リクエストヘッダ
 * @param in 依存性注入
 * @param admin 管理者権限
 */
class DeleteTask(implicit req: RequestHeader, in: Injections, admin: Boolean) {
	/**
	 * 指定された呼出符号の参加局を削除します。
	 *
	 * @param call 削除対象の呼出符号
	 * @return 管理画面のページ
	 */
	def delete(call: String): Html = util.Try {
		in.ats.drop(call)
		Logger(getClass).info(s"deleted: $call")
		pages.lists()
	}.getOrElse(pages.lists())

	/**
	 * 全ての参加局を削除します。
	 *
	 * @return 管理画面のページ
	 */
	def clear = {
		in.ats.deleteTables()
		in.ats.createTables()
		Logger(getClass).info("all records deleted")
		pages.lists()
	}
}


/**
 * アップロードされた交信記録がこの書類受付システムで処理可能か確認します。
 *
 *
 * @param req 交信記録を含むリクエスト
 * @param in 依存性注入
 */
class VerifyTask(implicit req: Request[AnyContent], in: Injections) {
	/**
	 * 交信記録を処理可能か確認して結果のメッセージを返します。
	 *
	 * @return 確認した結果を表すメッセージ
	 */
	def accept = util.Try {
		val logs = req.body.asMultipartFormData.get.files.map(_.ref)
		val bads = logs.map(in.ats.archives().decodable(_).isPresent())
		if (bads.exists(identity)) warns.unsup() else warns.ready()
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
 * @param in 依存性注入
 */
class FillInTask(implicit req: Request[AnyContent], in: Injections) {
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
 * @param in 依存性注入
 */
class FileDLTask(call: String, file: String)(implicit in: Injections) {
	/**
	 * 交信記録のファイルの内容を返します。
	 *
	 * @return 交信記録のバイト列
	 */
	def get = in.ats.archives().byCall(call).asScala.find(_.file == file).get.data
}


/**
 * 書類提出を受理した内容のメールを参加局に送信します。
 *
 *
 * @param in 依存性注入
 */
class NotifyTask(implicit in: Injections) {
	/**
	 * 指定された参加局に対してメールを送信します。
	 *
	 * @param station 参加局の登録情報
	 */
	def send(station: StationData): Unit = util.Try {
		val mail = new Email
		val user = in.cf.get[String]("play.mailer.user")
		val host = in.cf.get[String]("play.mailer.host")
		val text = views.txt.mails.accept(station.call).body.trim
		mail.setFrom("%s <%s@%s>".format(in.rule.host, user, host))
		mail.addTo("%s <%s>".format(station.call, station.mail))
		mail.addBcc(in.rule.mail)
		mail.addReplyTo(in.rule.mail)
		mail.setSubject(text.linesIterator.toSeq.head.split(";").head.trim)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		in.mc.send(mail)
	}.recover {
		case ex: EmailException => Logger("mail").error("MAIL ERROR!", ex)
	}
}


/**
 * リアルタイムコンテストのストリーミング接続を確立して、交信記録の受信と得点状況の配信を実行します。
 *
 *
 * @param out
 * @param token 参加局を識別するトークン
 * @param in 依存性注入
 */
class SocketTask(out: ActorRef, token: UUID)(implicit in: Injections) extends Actor {
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
		val station = in.ats.stations().byUUID(token).get(0)
		val ranking = in.ats.rankings().byCall(station.call)
		val qsoDiff = decoder.unpack(data.tail).asScala
		in.ats.messages().drop(station.call, qsoDiff.take(data.head.toInt & 0xFF).asJava)
		in.ats.messages().push(station.call, qsoDiff.drop(data.head.toInt & 0xFF).asJava)
		in.ats.update(station.call, in.rule);
		Logger(this.getClass).info(s"update: $station.call")
		if (in.rule.finish()) new RankingTableToJson().json else ""
	}
}
