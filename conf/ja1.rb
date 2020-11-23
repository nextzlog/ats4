# ALLJA1 CONTEST DEFINED by ATS-4

import 'qxsl.ruler.Contest'
import 'qxsl.ruler.RuleKit'

ALLJA1 = RuleKit.load('allja1.lisp').contest
CITYDB = ALLJA1.get('CITYDB').toList.select{|c| c.code.length > 3}

class ContestJA1 < Contest
	def initialize(sections)
		super(*sections.map{|s| s.cache})
	end
	def get(name)
		eval name
	end
	def getStartDay(year)
		ALLJA1.getStartDay(year)
	end
	def getFinalDay(year)
		ALLJA1.getFinalDay(year)
	end
	def getDeadLine(year)
		ALLJA1.getDeadLine(year)
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
