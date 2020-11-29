# REAL-TIME CONONLINE DEFINED by ATS-4

import 'java.time.LocalDate'
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
		date = LocalDate.now
		past = ONLINE.getStartDay(year + 0)
		come = ONLINE.getStartDay(year + 1)
		past.until(date).getMonths >= 6? come : past
	end
	def getFinalDay(year)
		date = LocalDate.now
		past = ONLINE.getFinalDay(year + 0)
		come = ONLINE.getFinalDay(year + 1)
		past.until(date).getMonths >= 6? come : past
	end
	def getDeadLine(year)
		date = LocalDate.now
		past = ONLINE.getDeadLine(year + 0)
		come = ONLINE.getDeadLine(year + 1)
		past.until(date).getMonths >= 6? come : past
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
