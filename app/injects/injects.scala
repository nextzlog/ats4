/*******************************************************************************
 * Amateur Radio Contest Administration System 'ATS-4' since 2017 April 2nd
 * License: GNU General Public License v3.0 (see LICENSE)
 * Author: Journal of Hamradio Informatics (https://pafelog.net)
*******************************************************************************/
package injects

import qxsl.ruler.{Program, RuleKit}

import play.api.inject.{bind, SimpleModule}

/**
 * コンテストの規約に対する依存性を注入します。
 *
 *
 * @author 無線部開発班
 *
 * @since 2022/07/24
 */
class Ats4Module extends SimpleModule((env, conf) => {
	val rule = RuleKit.load(conf.get[String]("ats4.rules"))
	List(bind(classOf[Program]).toInstance(rule.program()))
})
