/*******************************************************************************
 * Amateur Radio Contest Administration System 'ATS-4' since 2017 April 2nd
 * Released under the GNU General Public License (GPL) v3 (see LICENSE)
 * Univ. Tokyo Amateur Radio Club Development Task Force (nextzlog.dev)
*******************************************************************************/
package injects

import javax.inject.Inject

import qxsl.ruler.{Program, RuleKit}

import ats4.root.ATS

import play.api.Configuration
import play.api.db.Database
import play.api.i18n.{Lang, Messages, MessagesApi, MessagesImpl}
import play.api.inject.{bind, SimpleModule}
import play.libs.mailer.MailerClient


/**
 * 注入される依存性をまとめます。
 *
 *
 * @author 無線部開発班
 *
 * @since 2023/05/17
 */
trait Injections {
	/**
	 * 設定を参照します。
	 */
	implicit val cf: Configuration
	
	/**
	 * データベースを参照します。
	 */
	implicit val db: Database

	/**
	 * メールクライアントを参照します。
	 */
	implicit val mc: MailerClient

	/**
	 * 国際化の仕組みを参照します。
	 */
	implicit val api: MessagesApi

	/**
	 * コンテストのデータベースです。
	 */
	implicit val ats = new ATS(db.getConnection()).createTables()

	/**
	 * コンテストの規約です。
	 */
	implicit val rule = RuleKit.load(get("ats4.rules")).program()

	/**
	 * 指定された設定を返します。
	 *
	 *
	 * @param key 設定の名前
	 *
	 * @return 文字列
	 */
	def get(key: String): String = cf.get[String](key)

	/**
	 * デフォルトの言語の地域化を返します。
	 *
	 *
	 * @return 地域化
	 */
	def msg: Messages = MessagesImpl(Lang.defaultLang, api)
}


/**
 * 注入される依存性を受け取ります。
 *
 *
 * @author 無線部開発班
 *
 * @since 2023/05/17
 */
case class DefaultInjections @Inject()(
	cf: Configuration,
	db: Database,
	mc: MailerClient,
	api: MessagesApi,
) extends Injections


/**
 * 依存性を注入します。
 *
 *
 * @author 無線部開発班
 *
 * @since 2022/07/24
 */
class Ats4Module extends SimpleModule((env, conf) => {
	List(bind(classOf[Injections]).to[DefaultInjections])
})
