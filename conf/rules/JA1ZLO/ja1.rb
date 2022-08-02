# ALLJA1 CONTEST DEFINED by ATS-4

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

ALLJA1 = RuleKit.load('allja1.lisp').contest

class ProgramJA1 < Program
	def name()
		ALLJA1.name
	end
	def host()
		'東大無線部'
	end
	def mail()
		'allja1@ja1zlo.u-tokyo.org'
	end
	def link()
		'ja1zlo.u-tokyo.org/allja1'
	end
	def help()
		AssetUtil.root.string('rules/JA1ZLO/ja1.md')
	end
	def get(name)
		eval name
	end
	def year()
		opt_year(method(:getStartDay))
	end
	def getStartDay(year)
		schedule(year, 6, 4, 'SATURDAY')
	end
	def getFinalDay(year)
		schedule(year, 6, 4, 'SATURDAY')
	end
	def getDeadLine(year)
		schedule(year, 7, 3, 'SATURDAY')
	end
	def limitMultipleEntry(code)
		1
	end
	def conflict(entries)
		muls = entries.map{|e| e.name.include?("団体")}
		alls = entries.map{|e| e.name.include?("総合")}
		in1s = entries.map{|e| e.name.include?("1エリア内")}
		js1s = entries.map{|e| /1エリア内 .+ 総合/.match?(e.name)}
		return true if muls.any? and not muls.all?
		return true if in1s.any? and not js1s.any?
		not alls.any? and not entries.empty?
	end
end

# returns contest definition
ProgramJA1.new(*ALLJA1)
