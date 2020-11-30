# CONTEST UTILITIES DEFINED by ATS-4

import 'java.time.DayOfWeek'
import 'java.time.LocalDate'
import 'java.time.ZoneId'
import 'java.time.temporal.TemporalAdjusters'

def schedule(year, month, nth, dayOfWeek)
	week = DayOfWeek.valueOf(dayOfWeek)
	date = LocalDate.of(year, month, 1)
	date.with(TemporalAdjusters.dayOfWeekInMonth(nth, week))
end

def opt_start_day(start_day, year, span = 6)
	day = start_day.call(year + 0)
	nex = start_day.call(year + 1)
	exp = day.until(LocalDate.now)
	exp.getMonths >= span ? nex : day
end

def opt_Final_day(final_day, year, span = 6)
	day = final_day.call(year)
	nex = final_day.call(year + 1)
	exp = day.until(LocalDate.now)
	exp.getMonths >= span ? nex : day
end

def opt_dead_line(dead_line, year, span = 6)
	day = dead_line.call(year)
	nex = dead_line.call(year + 1)
	exp = day.until(LocalDate.now)
	exp.getMonths >= span ? nex : day
end
