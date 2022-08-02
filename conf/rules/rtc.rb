# REAL-TIME CONONLINE DEFINED by ATS-4

java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.utils.AssetUtil'

require 'rules/util'

ONLINE = RuleKit.load('online.lisp').contest

class ProgramRTC < Program
	def name()
		'リアルタイムコンテスト'
	end
	def host()
		'東大無線部'
	end
	def mail()
		'allja1@ja1zlo.u-tokyo.org'
	end
	def link()
		'ja1zlo.u-tokyo.org/rt'
	end
	def help()
		AssetUtil.root.string('rules/rtc.md')
	end
	def get(name)
		eval name
	end
	def year()
		opt_year(method(:getStartDay))
	end
	def getStartDay(year)
		schedule(year, 7, 4, 'MONDAY')
	end
	def getFinalDay(year)
		schedule(year, 12, 4, 'MONDAY')
	end
	def getDeadLine(year)
		schedule(year, 12, 4, 'MONDAY')
	end
	def finish(zone)
		true
	end
	def limitMultipleEntry(code)
		1
	end
	def conflict(entries)
		mul = entries.map{|e| e.name.include?("団体")}.any?
		sin = entries.map{|e| e.name.include?("個人")}.any?
		mul && sin
	end
end

# returns contest definition
ProgramRTC.new(*ONLINE)
