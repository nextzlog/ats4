# ALLJA1 CONTEST DEFINED for ATS-4

require 'rules/ats'

JA1 = RuleKit.load('allja1.lisp').contest

NAME = JA1.name
HOST = '東大無線部'
MAIL = 'allja1@ja1zlo.u-tokyo.org'
LINK = 'ja1zlo.u-tokyo.org/allja1'

JS1 = /1エリア内 .+ 総合/
IN1 = /1エリア内/

MUL = /団体/
ALL = /総合/

class ProgramJA1 < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 6, 4, DayOfWeek::SATURDAY)
		JA1.each{|section| add(section)}
	end

	def help()
		AssetUtil.root.string('rules/ja1.md')
	end

	def getDeadLine(year)
		getStartDay(year).plusWeeks(3)
	end

	def conflict(entries)
		muls = entries.map{|e| MUL.match?(e.name)}
		alls = entries.map{|e| ALL.match?(e.name)}
		in1s = entries.map{|e| IN1.match?(e.name)}
		js1s = entries.map{|e| JS1.match?(e.name)}
		return true if muls.any? and not muls.all?
		return true if in1s.any? and not js1s.any?
		not alls.any? and not entries.empty?
	end
end

RULE = ProgramJA1.new
RULE
