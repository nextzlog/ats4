# REAL-TIME CONTEST DEFINED for ATS-4

java_import 'java.time.DayOfWeek'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.utils.AssetUtil'

LISP = <<-EOS
(load "qxsl/ruler/online.lisp")
(defun time? it #t)
RT
EOS

ONLINE = RuleKit.forName('elva').eval(LISP).contest

class ProgramRTC < Program::Annual
	def initialize()
		super(7, 4, DayOfWeek::MONDAY)
		ONLINE.each{|section| add(section)}
	end

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

	def getFinalDay(year)
		getStartDay(year).plusMonths(5)
	end

	def getDeadLine(year)
		getStartDay(year).plusMonths(5)
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
ProgramRTC.new
