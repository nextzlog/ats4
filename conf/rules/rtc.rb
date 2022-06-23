# REAL-TIME CONONLINE DEFINED by ATS-4

java_import 'qxsl.ruler.Contest'
java_import 'qxsl.ruler.RuleKit'

require 'rules/util'

ONLINE = RuleKit.load('online.lisp').contest

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
	def finish(year, zone)
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
