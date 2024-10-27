# REAL-TIME CONTEST DEFINED for ATS-4

require 'rules/ats'

LISP = <<-EOS
(load "qxsl/ruler/online.lisp")
(defun time? it #t)
RT
EOS

RTC = RuleKit.forName('elva').eval(LISP).contest

NAME = 'リアルタイムコンテスト'
HOST = '東大無線部'
MAIL = 'allja1.info@gmail.com'
LINK = 'ja1zlo.u-tokyo.org/rt'

class ProgramRTC < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 7, 4, DayOfWeek::MONDAY)
		setHelp(AssetUtil.root.string('rules/rtc.md'))
		RTC.each{|section| add(section)}
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

	def conflict(entries)
		mul = entries.map{|e| e.name.include?('団体')}.any?
		sin = entries.map{|e| e.name.include?('個人')}.any?
		mul && sin
	end
end

RULE = ProgramRTC.new
RULE
