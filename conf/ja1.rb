# ALLJA1 CONTEST DEFINED by ATS-4

import 'java.io.InputStreamReader'
import 'java.time.DayOfWeek'
import 'java.time.LocalDate'
import 'java.time.Month'
import 'java.time.temporal.TemporalAdjusters'
import 'java.util.stream.Collectors'
import 'qxsl.local.LocalCityBase'
import 'qxsl.ruler.Contest'
import 'qxsl.ruler.RuleKit'

# ALLJA1 rules
stream = RuleKit.java_class.resource_as_stream('allja1.lisp')
reader = InputStreamReader.new(stream, 'UTF-8')
ALLJA1 = RuleKit.load('elva').contest(reader)
reader.close()

def date(year, month, dayOfWeek, nth)
	week = DayOfWeek.valueOf(dayOfWeek)
	date = LocalDate.of(year, Month.valueOf(month), 1)
	date.with(TemporalAdjusters.dayOfWeekInMonth(nth, week))
end

CITYDB = LocalCityBase.load('city.ja').toList
stream = CITYDB.stream().filter(->(c) {c.getCode.length > 2})
CITIES = stream.collect(Collectors.toList)

# extends Contest class to access global variables defined here
class ExtendedALLJA1 < Contest
	def initialize()
		super(*ALLJA1.getSections().toArray())
	end
	def get(name)
		eval name
	end
	def invoke(name, args)
		method(name).call(*args)
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
	def getName()
		ALLJA1.getName()
	end
	def getHost()
		ALLJA1.getHost()
	end
	def getMail()
		ALLJA1.getMail()
	end
	def getLink()
		ALLJA1.getLink()
	end
end

# returns redefined ALLJA1 contest
TEST = ExtendedALLJA1.new
