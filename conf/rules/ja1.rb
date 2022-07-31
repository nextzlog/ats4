# ALLJA1 CONTEST DEFINED by ATS-4

java_import 'qxsl.ruler.Contest'
java_import 'qxsl.ruler.RuleKit'

require 'rules/util'

ALLJA1 = RuleKit.load('allja1.lisp').contest

class ContestJA1 < Contest
	def initialize(sections)
		super(*sections.map{|s| s.cache})
	end
	def name()
		ALLJA1.name
	end
	def host()
		ALLJA1.host
	end
	def mail()
		ALLJA1.mail
	end
	def link()
		ALLJA1.link
	end
	def get(name)
		eval name
	end
	def year()
		opt_year(method(:getStartDay))
	end
	def getStartDay(year)
		ALLJA1.method(:getStartDay).call(year)
	end
	def getFinalDay(year)
		ALLJA1.method(:getFinalDay).call(year)
	end
	def getDeadLine(year)
		ALLJA1.method(:getDeadLine).call(year)
	end
	def limitMultipleEntry(code)
		ALLJA1.limitMultipleEntry(code)
	end
	def conflict(entries)
		muls = entries.map{|e| e.name.include?("団体")}
		alls = entries.map{|e| e.name.include?("総合")}
		in1s = entries.map{|e| e.name.include?("1エリア内")}
		js1s = entries.map{|e| /1エリア内 .+ 総合/.match?(e.name)}
		return true if muls.any? and not muls.all?
		return true if in1s.any? and not js1s.any?
		not alls.any? and not entries.empty?
	end
end

# returns contest definition
ContestJA1.new(ALLJA1)
