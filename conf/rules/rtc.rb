# REAL-TIME CONONLINE DEFINED by ATS-4

java_import 'qxsl.ruler.Contest'
java_import 'qxsl.ruler.RuleKit'

require 'rules/util'

ONLINE = RuleKit.load('online.lisp').contest

class ContestRTC < Contest
	def initialize(sections)
		super(*sections.map{|s| s.cache})
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
	def get(name)
		eval name
	end
	def year()
		opt_year(method(:getStartDay))
	end
	def getStartDay(year)
		schedule(year, 7, 4, 'MONDAY')
	end
	def getFinalDay(year)
		schedule(year, 12, 4, 'MONDAY')
	end
	def getDeadLine(year)
		schedule(year, 12, 4, 'MONDAY')
	end
	def finish(zone)
		true
	end
	def limitMultipleEntry(code)
		ONLINE.limitMultipleEntry(code)
	end
	def conflict(entries)
		mul = entries.map{|e| e.name.include?("団体")}.any?
		sin = entries.map{|e| e.name.include?("個人")}.any?
		mul && sin
	end
end

# returns contest definition
ContestRTC.new(ONLINE)
