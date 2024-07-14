/*******************************************************************************
 * Amateur Radio Contest Administration System 'ATS-4' since 2017 April 2nd
 * Released under the GNU General Public License (GPL) v3 (see LICENSE)
 * Univ. Tokyo Amateur Radio Club Development Task Force (nextzlog.dev)
*******************************************************************************/
package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import ats4.root.{ATS, CSV}

import scala.concurrent.{Future, ExecutionContext => EC}
import scala.concurrent.duration._

import play.api.Logger
import play.api.http.HttpErrorHandler
import play.api.libs.streams.ActorFlow
import play.api.mvc.{InjectedController => IC, RequestHeader => RH, _}
import play.api.mvc.Results.{Forbidden, InternalServerError, Status}

import injects.Injections
import models._
import tasks._
import views.html.pages

import akka.actor.{ActorSystem, Props}
import akka.stream.{Materializer => Mat}
import akka.stream.scaladsl.{BroadcastHub, Flow, Keep, MergeHub}

/**
 * 管理者権限を伴わず、公開画面のGETメソッドを処理するコントローラです。
 *
 *
 * @param in 依存性注入
 */
@Singleton
class Index @Inject()(implicit in: Injections) extends IC {
	/**
	 * 非管理者権限を表す真偽値です。
	 */
	implicit val admin: Boolean = false

	/**
	 * トップページのビューを返します。
	 *
	 * @return トップページ
	 */
	def index = Action(Ok(pages.index()))

	/**
	 * 書類提出のページのビューを返します。
	 *
	 * @return 書類提出のページ
	 */
	def entry = Action(implicit r => Ok(pages.entry(new ContestForm)))

	/**
	 * 暫定結果のページのビューを返します。
	 *
	 * @return 暫定結果のページ
	 */
	def lists = Action(implicit r => Ok(pages.lists()))

	/**
	 * 全参加局のページのビューを返します。
	 *
	 * @return 全参加局のページ
	 */
	def calls = Action(Ok(pages.calls()))

	/**
	 * 皆様の声のページのビューを返します。
	 *
	 * @return 皆様の声のページ
	 */
	def forum = Action(Ok(pages.forum()))

	/**
	 * ヘルプページのビューを返します。
	 *
	 * @return ヘルプページ
	 */
	def guide = Action(Ok(pages.guide()))
}


/**
 * 管理者権限を伴わず、公開画面のPOSTメソッドを処理するコントローラです。
 *
 *
 * @param in 依存性注入
 */
@Singleton
class Entry @Inject()(implicit in: Injections) extends IC {
	/**
	 * 非管理者権限を表す真偽値です。
	 */
	implicit val admin: Boolean = false

	/**
	 * 書類提出のリクエストを処理して、確認画面のページのビューを返します。
	 *
	 * @return 確認画面または再提出のページ
	 */
	def entry = Action(implicit r => Ok(new UploadTask().accept))

	/**
	 * 書式検査のリクエストを処理して、検査結果のメッセージを返します。
	 *
	 * @return 検査結果のメッセージ
	 */
	def trial = Action(implicit r => Ok(new VerifyTask().accept))

	/**
	 * サマリーシートからフォームの初期値を抽出し、JSONで返します。
	 *
	 * @return JSONの文字列
	 */
	def unbox = Action(implicit r => Ok(new FillInTask().accept))
}


/**
 * 管理者権限を伴って、管理画面のGETメソッドを処理するコントローラです。
 *
 *
 * @param in 依存性注入
 */
@Singleton
class Admin @Inject()(implicit in: Injections) extends IC {
	/**
	 * 管理者権限を表す真偽値です。
	 */
	implicit val admin: Boolean = true

	/**
	 * 管理画面のページのビューを返します。
	 *
	 * @return 管理画面のページ
	 */
	def index = Action(implicit r => Ok(pages.lists()))

	/**
	 * 書類提出のページのビューを返します。
	 *
	 * @return 書類提出のページ
	 */
	def entry = Action(implicit r => Ok(pages.entry(new ContestForm)))

	/**
	 * 集計結果のページのビューを返します。
	 *
	 * @return 集計結果のページ
	 */
	def excel = Action(Ok(new CSV(in.ats, in.rule).dump()))

	/**
	 * 指定された呼出符号の参加局の専用の書類提出のページのビューを返します。
	 *
	 * @param call 提出対象の呼出符号
	 * @return 書類提出のページ
	 */
	def amend(call: String) = Action(implicit r => Ok(pages.amend(call)))

	/**
	 * 指定された呼出符号の参加局の専用の暫定結果のページのビューを返します。
	 *
	 * @param call 確認対象の呼出符号
	 * @return 暫定結果のページ
	 */
	def proof(call: String) = Action(implicit r => Ok(pages.proof(call)))

	/**
	 * 指定された呼出符号の参加局の、指定された交信記録のファイルを返します。
	 *
	 * @param call 確認対象の呼出符号
	 * @param file 確認対象のファイル名
	 * @return 交信記録のデータ
	 */
	def table(call: String, file: String) = Action(Ok(new FileDLTask(call, file).get))
}


/**
 * 管理者権限を伴って、管理画面のPOSTメソッドを処理するコントローラです。
 *
 *
 * @param in 依存性注入
 */
@Singleton
class Force @Inject()(implicit in: Injections) extends IC {
	/**
	 * 管理者権限を表す真偽値です。
	 */
	implicit val admin: Boolean = true

	/**
	 * 全ての参加局の情報を削除します。
	 *
	 * @return 管理画面のページ
	 */
	def init = Action(implicit r => Ok(new DeleteTask().clear))

	/**
	 * 指定された呼出符号の参加局の情報を削除します。
	 *
	 * @param call 削除対象の呼出符号
	 * @return 管理画面のページ
	 */
	def drop(call: String) = Action(implicit r => Ok(new DeleteTask().delete(call)))

	/**
	 * 書類提出のリクエストを処理して、確認画面のページのビューを返します。
	 *
	 * @return 確認画面または再提出のページ
	 */
	def edit = Action(implicit r => Ok(new UploadTask().accept))

	/**
	 * 全ての参加局の得点を再計算して、確認画面のページのビューを返します。
	 *
	 * @return 管理画面のページ
	 */
	def redo = Action(implicit r => Ok(new UpdateTask().accept))
}


/**
 * 管理者権限を伴って、開発画面のGETまたはPOSTメソッドを処理するコントローラです。
 *
 *
 * @param in 依存性注入
 */
@Singleton
class Shell @Inject()(implicit in: Injections) extends IC {
	/**
	 * 管理者権限を表す真偽値です。
	 */
	implicit val admin: Boolean = true

	/**
	 * 開発画面のページのビューを返します。
	 *
	 * @return 管理画面のページ
	 */
	def shell = Action(implicit r => Ok(pages.shell(new DevelopForm().fill(DevelopFormData.data))))

	/**
	 * 送信された規約を評価して、実行結果のページのビューを返します。
	 *
	 * @return 管理画面のページ
	 */
	def debug = Action(implicit r => Ok(pages.debug(new DevelopForm().bindFromRequest())))
}


/**
 * ブラウザに返信するレスポンスのキャッシュ設定を編集するフィルタです。
 *
 *
 * @param mat
 * @param ec
 * @param in 依存性注入
 */
@Singleton
class Cache @Inject()(implicit val mat: Mat, ec: EC, in: Injections) extends Filter {
	def apply(next: RH => Future[Result])(header: RH) = next(header).map((r: Result) => {
		r.withHeaders("Cache-Control" -> in.get("cache.control"))
	})
}


/**
 * クライアント側のエラーまたはサーバ側のエラーをクライアント側に通知するハンドラです。
 *
 *
 * @param in 依存性注入
 */
@Singleton
class Error @Inject()(implicit in: Injections) extends HttpErrorHandler {
	/**
	 * 非管理者権限を表す真偽値です。
	 */
	implicit val admin: Boolean = false

	/**
	 * クライアント側のエラーを表示するページを返します。
	 *
	 * @param r リクエストヘッダ
	 * @param st エラーコード
	 * @param msg エラーの文字列
	 * @return エラーを表示するページ
	 */
	override def onClientError(r: RH, st: Int, msg: String) = {
		Future.successful(Status(st)(pages.index(Some(st))))
	}

	/**
	 * サーバ側のエラーを表示するページを返します。
	 *
	 * @param r リクエストヘッダ
	 * @param throwable 例外
	 * @return エラーを表示するページ
	 */
	override def onServerError(r: RH, throwable: Throwable) = {
		Logger(classOf[Error]).error("SERVER ERROR!", throwable)
		Future.successful(InternalServerError(pages.index(Some(500))))
	}
}


/**
 * リアルタイムコンテストのストリーミング接続のリクエストを受け付けます。
 *
 *
 * @param as アクター環境
 * @param mat
 * @param in 依存性注入
 */
@Singleton
class Agent @Inject()(implicit as: ActorSystem, mat: Mat, in: Injections) {
	val (sink, src) = MergeHub.source[Array[Byte]].toMat(BroadcastHub.sink)(Keep.both).run()
	val bus = Flow.fromSinkAndSource(sink, src).delay(in.cf.get[Int]("rtc.delay").second)

	/**
	 * 指定されたトークンに対応する参加局に対してストリーミング接続を確立します。
	 *
	 * @param uuid 参加局を識別するトークン
	 * @return ストリーミング接続
	 */
	def agent(uuid: UUID) = WebSocket.acceptOrResult[Array[Byte], Array[Byte]](req =>
		Future.successful(if (in.rule.accept() && !in.ats.stations().byUUID(uuid).isEmpty()) {
			Right(ActorFlow.actorRef(out => Props(new SocketTask(out, uuid))).viaMat(bus)(Keep.right))
		} else Left(Forbidden))
	)
}
