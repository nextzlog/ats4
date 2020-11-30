# REAL-TIME CONONLINE DEFINED by ATS-4

import 'qxsl.ruler.Contest'
import 'qxsl.ruler.RuleKit'

require 'rules/util'

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
		opt_start_day(ONLINE.method(:getStartDay), year)
	end
	def getFinalDay(year)
		opt_final_day(ONLINE.method(:getFinalDay), year)
	end
	def getDeadLine(year)
		opt_dead_line(ONLINE.method(:getStartDay), year)
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
