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
		mul = entries.map{|e| e.name.include?("団体")}.any?
		sin = entries.map{|e| e.name.include?("個人")}.any?
		mul && sin
	end
end

# returns contest definition
ContestJA1.new(ALLJA1)
