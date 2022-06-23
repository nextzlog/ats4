# ALLJA1 CONTEST DEFINED by ATS-4

java_import 'qxsl.ruler.Contest'
java_import 'qxsl.ruler.RuleKit'

require 'rules/util'

ALLJA1 = RuleKit.load('allja1.lisp').contest

class ContestJA1 < Contest
	def initialize(sections)
		super(*sections.map{|s| s.cache})
	end
	def get(name)
		eval name
	end
	def getStartDay(year)
		opt_start_day(ALLJA1.method(:getStartDay), year)
	end
	def getFinalDay(year)
		opt_final_day(ALLJA1.method(:getFinalDay), year)
	end
	def getDeadLine(year)
		opt_dead_line(ALLJA1.method(:getDeadLine), year)
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
end

TEST = ContestJA1.new(ALLJA1)
