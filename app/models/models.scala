/*******************************************************************************
 * Amateur Radio Contest Administration System 'ATS-4' since 2017 April 2nd
 * License: GNU General Public License v3.0 (see LICENSE)
 * Author: Journal of Hamradio Informatics (https://pafelog.net)
*******************************************************************************/
package models

import java.util.UUID

import qxsl.draft.Call
import qxsl.ruler._
import qxsl.sheet.SheetDecoder

import ats4.data._
import ats4.root.ATS

import scala.jdk.CollectionConverters._

import play.api.data.{Form, Forms, OptionalMapping}
import play.api.libs.json.{Json, JsValue}

/**
 * 部門選択のフォームに入力されたデータです。
 *
 *
 * @param sect 部門
 * @param city 運用場所
 */
case class SectionFormData(
	sect: String,
	city: String
)


/**
 * 共通事項のフォームに入力されたデータです。
 *
 *
 * @param call 呼出符号
 * @param name 名前
 * @param post 郵便番号
 * @param addr 郵便宛先
 * @param mail メールアドレス
 * @param note 感想
 * @param uuid リアルタイムコンテストのトークン
 */
case class StationFormData(
	call: String,
	name: String,
	post: String,
	addr: String,
	mail: String,
	note: String,
	uuid: UUID
)


/**
 * 交信記録のフォームに入力されたデータです。
 *
 *
 * @param file このファイルの名前
 * @param keep このファイルを使う場合は真
 */
case class ArchiveFormData(
	file: String,
	keep: Boolean
)


/**
 * 書類提出のフォームに入力されたデータです。
 *
 *
 * @param station 共通事項のデータ
 * @param entries 部門選択のデータ
 * @param uploads 交信記録のデータ
 */
case class ContestFormData(
	station: StationFormData,
	entries: Seq[SectionFormData],
	uploads: Seq[ArchiveFormData]
)


/**
 * 書類提出のフォームの初期値をデータベースから取得します。
 */
object ContestFormData {
	/**
	 * 指定された呼出符号の参加局の登録情報を返します。
	 *
	 * @param call 確認対象の呼出符号
	 * @param ats データベースの依存性注入
	 * @param contest コンテスト規約の依存性注入
	 * @return 初期値が入力されたフォームデータ
	 */
	def apply(call: String)(implicit ats: ATS, contest: Contest) = {
		val station = ats.stations().byCall(call).get(0)
		val entries = ats.rankings().byCall(call).asScala.toList
		val uploads = ats.archives().byCall(call).asScala.toList
		new ContestFormData(
			StationFormData(
				call = station.call,
				name = station.name,
				post = station.post,
				addr = station.addr,
				mail = station.mail,
				note = station.note,
				uuid = UUID.fromString(station.uuid)
			),
			entries.map(r => new SectionFormData(r.sect, r.city)),
			uploads.map(a => new ArchiveFormData(a.file, true))
		)
	}
}


/**
 * 部門選択のフォームとデータの関連付けと検証を実装します。
 *
 *
 * @param ats データベースの依存性注入
 * @param contest コンテスト規約の依存性注入
 */
class SectionForm(implicit ats: ATS, contest: Contest) extends Form[SectionFormData](
	Forms.mapping(
		"sect" -> Forms.text,
		"city" -> Forms.text
	)
	(SectionFormData.apply)
	(SectionFormData.unapply).verifying(s => {
		contest.section(s.sect).isAbsence() ||
		contest.section(s.sect).getCityList().asScala.exists(_.toString == s.city)
	}), Map.empty, Nil, None
)


/**
 * 共通事項のフォームとデータの関連付けと検証を実装します。
 *
 *
 * @param ats データベースの依存性注入
 * @param contest コンテスト規約の依存性注入
 */
class StationForm(implicit ats: ATS, contest: Contest) extends Form[StationFormData](
	Forms.mapping(
		"call" -> Forms.nonEmptyText
			.verifying(Call.isValid(_))
			.transform(new Call(_).value(), identity[String]),
		"name" -> Forms.nonEmptyText,
		"post" -> Forms.nonEmptyText,
		"addr" -> Forms.nonEmptyText,
		"mail" -> Forms.email,
		"note" -> Forms.text,
		"uuid" -> OptionalMapping(Forms.uuid)
			.transform(_.getOrElse(ats.stations().createUUID()), Some[UUID](_))
	)
	(StationFormData.apply)
	(StationFormData.unapply), Map.empty, Nil, None
)


/**
 * 交信記録のフォームとデータの関連付けと検証を実装します。
 *
 *
 * @param ats データベースの依存性注入
 * @param contest コンテスト規約の依存性注入
 */
class ArchiveForm(implicit ats: ATS, contest: Contest) extends Form[ArchiveFormData](
	Forms.mapping(
		"file" -> Forms.nonEmptyText,
		"keep" -> Forms.boolean,
	)
	(ArchiveFormData.apply)
	(ArchiveFormData.unapply), Map.empty, Nil, None
)


/**
 * 書類提出のフォームとデータの関連付けと検証を実装します。
 *
 *
 * @param ats データベースの依存性注入
 * @param contest コンテスト規約の依存性注入
 */
class ContestForm(implicit ats: ATS, contest: Contest) extends Form[ContestFormData](
	Forms.mapping(
		"station" -> new StationForm().mapping,
		"entries" -> Forms.seq(new SectionForm().mapping).verifying(new Conflict().ok(_)),
		"uploads" -> Forms.seq(new ArchiveForm().mapping)
	)
	(ContestFormData.apply)
	(ContestFormData.unapply), Map.empty, Nil, None
)


/**
 * 部門選択のフォームの整合性を検証します。
 *
 *
 * @param contest コンテスト規約の依存性注入
 */
class Conflict(implicit contest: Contest) {
	/**
	 * 指定された部門選択の整合性を検証します。
	 *
	 * @param sectionList 部門選択のリスト
	 * @return 受理可能な場合は真
	 */
	def ok(sectionList: Seq[SectionFormData]): Boolean = {
		val rules = sectionList.map(_.sect).map(contest.section)
		! contest.conflict(rules.filterNot(_.isAbsence).toArray)
	}
}


/**
 * 全ての部門における全ての参加局の得点状況を格納したJSONデータを生成します。
 *
 *
 * @param ats データベースの依存性注入
 * @param contest コンテスト規約の依存性注入
 */
class RankingTableToJson(implicit ats: ATS, contest: Contest) {
	/**
	 * JSONの文字列を生成します。
	 *
	 * @return JSONの文字列
	 */
	def json = Json.stringify(toJS(contest))

	/**
	 * 指定された規約に対し、得点状況を格納したJSONの文字列を生成します。
	 *
	 * @param rule 対象の規約
	 * @return JSONの文字列
	 */
	def toJS(rule: Contest): JsValue = {
		Json.toJson(rule.asScala.toSeq.map(s => s.name() -> toJS(s)).toMap)
	}

	/**
	 * 指定された部門に対し、得点状況を格納したJSONの文字列を生成します。
	 *
	 * @param rule 対象の部門
	 * @return JSONの文字列
	 */
	def toJS(rule: Section): JsValue = {
		Json.toJson(ats.rankings().bySect(rule).asScala.map(toJS))
	}

	/**
	 * 指定された参加局に対し、得点状況を格納したJSONの文字列を生成します。
	 *
	 * @param data 対象の参加局
	 * @return JSONの文字列
	 */
	def toJS(data: RankingData): JsValue = Json.toJson(Map(
		"call" -> Json.toJson(data.call),
		"score" -> Json.toJson(data.score),
		"total" -> Json.toJson(data.total)
	))
}


/**
 * JARLサマリーシートの内容を抽出して、JSONデータに変換します。
 *
 *
 * @param decoder 対象のサマリーシート
 */
class SheetDecoderToJson(decoder: SheetDecoder) {
	/**
	 * サマリーシートの内容をJSONの文字列に変換します。
	 *
	 * @return JSONの文字列
	 */
	def json = Json.stringify(Json.toJson(Map(
		"call" -> decoder.getString("CALLSIGN"),
		"name" -> decoder.getString("NAME"),
		"addr" -> decoder.getString("ADDRESS"),
		"mail" -> decoder.getString("EMAIL"),
		"note" -> decoder.getString("COMMENTS")
	)))
}
