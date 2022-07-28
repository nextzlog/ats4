# CONTEST UTILITIES DEFINED by ATS-4

java_import 'java.time.DayOfWeek'
java_import 'java.time.LocalDate'
java_import 'java.time.Year'
java_import 'java.time.ZoneId'
java_import 'java.time.temporal.TemporalAdjusters'

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
