# ALLJA1 CONTEST DEFINED by ATS-4

import 'java.io.InputStreamReader'
import 'java.time.DayOfWeek'
import 'java.time.LocalDate'
import 'java.time.Month'
import 'java.time.temporal.TemporalAdjusters'
import 'qxsl.ruler.Contest'
import 'qxsl.ruler.RuleKit'
import 'qxsl.extra.field.City'

# ALLJA1 rules
stream = RuleKit.java_class.resource_as_stream('allja1.lisp')
reader = InputStreamReader.new(stream, 'UTF-8')
ALLJA1 = RuleKit.load('elva').contest(reader)
reader.close()

# contest settings
NAME = 'ALLJA1'
HOST = '東大無線部'
MAIL = 'allja1@ja1zlo.u-tokyo.org'
SITE = 'ja1zlo.u-tokyo.org'
RULE = 'ja1zlo.u-tokyo.org/allja1/%02drule.html'

# output directory
OUTPUT = 'rcvd'
REPORT = 'report.csv'

def date(year, month, dayOfWeek, nth)
	week = DayOfWeek.valueOf(dayOfWeek)
	date = LocalDate.of(year, Month.valueOf(month), 1)
	date.with(TemporalAdjusters.dayOfWeekInMonth(nth, week))
end

# schedule
def starting(year)
	date(year, 'JUNE', 'SATURDAY', 4)
end

def deadline(year)
	date(year, 'JULY', 'SATURDAY', 3)
end

# email
MAIL_INTERVAL_MINUTES = 5

# routines for ATS-4
def 総合部門(sects)
	団体 = sects.any?{|sect| sect.include? '団体'}
	関東 = sects.any?{|sect| sect.include? '1エリア内'}
	format('1エリア%s %s 総合 部門', 関東 ? '内' : '外', 団体 ? '団体' : '個人')
end

def 運用場所(部門, 市区町村)
	関東 = 部門.include?('1エリア内')
	県名 = 市区町村.split(/(?<=都|道|府|県)/).first
	(City.forCode('area', 県名).getFullPath()[2] == '関東') == 関東
end

# extends Contest class to access global variables defined here
class ExtendedALLJA1 < Contest
	def initialize()
		super(*ALLJA1.getSections().toArray())
	end
	def getName()
		ALLJA1.getName()
	end
	def get(name)
		eval name
	end
	def invoke(name, args)
		method(name).call(*args)
	end
end

# returns redefined ALLJA1 contest
ExtendedALLJA1.new
