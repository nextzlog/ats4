# REAL-TIME CONONLINE DEFINED by ATS-4

java_import 'java.time.DayOfWeek'
java_import 'java.time.LocalDate'
java_import 'java.time.Year'
java_import 'java.time.temporal.TemporalAdjusters'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.utils.AssetUtil'

def schedule(year, month, nth, dayOfWeek)
	week = DayOfWeek.valueOf(dayOfWeek)
	date = LocalDate.of(year, month, 1)
	date.with(TemporalAdjusters.dayOfWeekInMonth(nth, week))
end

def opt_year(func_start_day, months = 9)
	year = Year.now.getValue
	date = func_start_day.call(year)
	span = date.until(LocalDate.now)
	(span.getMonths > months ? 1: 0) + year
end

ONLINE = RuleKit.load('online.lisp').contest

class ProgramRTC < Program
	def name()
		'リアルタイムコンテスト'
	end
	def host()
		'東大無線部'
	end
	def mail()
		'allja1@ja1zlo.u-tokyo.org'
	end
	def link()
		'ja1zlo.u-tokyo.org/rt'
	end
	def help()
		AssetUtil.root.string('rules/JA1ZLO/rtc.md')
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
		1
	end
	def conflict(entries)
		mul = entries.map{|e| e.name.include?("団体")}.any?
		sin = entries.map{|e| e.name.include?("個人")}.any?
		mul && sin
	end
end

# returns contest definition
ProgramRTC.new(*ONLINE)
