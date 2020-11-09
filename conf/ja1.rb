# ALLJA1 CONTEST DEFINED by ATS-4

import 'java.time.DayOfWeek'
import 'java.time.LocalDate'
import 'java.time.Month'
import 'java.time.temporal.TemporalAdjusters'
import 'qxsl.ruler.Contest'
import 'qxsl.ruler.RuleKit'

# ALLJA1 rules
ALLJA1 = RuleKit.load('allja1.lisp').contest

def date(year, month, dayOfWeek, nth)
	week = DayOfWeek.valueOf(dayOfWeek)
	date = LocalDate.of(year, Month.valueOf(month), 1)
	date.with(TemporalAdjusters.dayOfWeekInMonth(nth, week))
end

CITYDB = ALLJA1.get("CITYDB").toList.select{|c| c.code.length > 2}

# extends Contest class to access global variables defined here
class ExtendedALLJA1 < Contest
	def initialize()
		super(*ALLJA1)
	end
	def get(name)
		eval name
	end
	def getStartDay(year)
		date(year, 'JUNE', 'SATURDAY', 4)
	end
	def getFinalDay(year)
		getStartDay(year)
	end
	def getDeadLine(year)
		date(year, 'JULY', 'SATURDAY', 3)
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

# returns redefined ALLJA1 contest
TEST = ExtendedALLJA1.new
