/*******************************************************************************
 * Amateur Radio Contest Administration System 'ATS-4' since 2017 April 2nd
 * Released under the GNU General Public License (GPL) v3 (see LICENSE)
 * Univ. Tokyo Amateur Radio Club Development Task Force (nextzlog.dev)
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
