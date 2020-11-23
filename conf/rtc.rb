# REAL-TIME CONONLINE DEFINED by ATS-4

import 'qxsl.ruler.Contest'
import 'qxsl.ruler.RuleKit'

ONLINE = RuleKit.load('online.lisp').contest
CITYDB = ONLINE.get('CITYDB').toList.select{|c| c.code.length <= 3 and not ['01', '48'].include?(c.code)}

class ContestRTC < Contest
	def initialize(sections)
		super(*sections.map{|s| s.cache})
	end
	def get(name)
		eval name
	end
	def getStartDay(year)
		ONLINE.getStartDay(year)
	end
	def getFinalDay(year)
		ONLINE.getFinalDay(year)
	end
	def getDeadLine(year)
		ONLINE.getDeadLine(year)
	end
	def openResults(year, zone)
		true
	end
	def name()
		ONLINE.name
	end
	def host()
		ONLINE.host
	end
	def mail()
		ONLINE.mail
	end
	def link()
		ONLINE.link
	end
end

TEST = ContestRTC.new(ONLINE)
