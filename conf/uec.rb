# UEC CONTEST DEFINED by ATS-4

import 'java.io.InputStreamReader'
import 'java.time.DayOfWeek'
import 'java.time.LocalDate'
import 'java.time.Month'
import 'java.time.ZoneId'
import 'java.time.temporal.TemporalAdjusters'
import 'java.util.stream.Collectors'
import 'qxsl.draft.Band'
import 'qxsl.local.LocalCityBase'
import 'qxsl.ruler.Contest'
import 'qxsl.ruler.Failure'
import 'qxsl.ruler.RuleKit'
import 'qxsl.ruler.Section'
import 'qxsl.ruler.Success'

# JAUTIL library
stream = RuleKit.java_class.resource_as_stream('jautil.lisp')
reader = InputStreamReader.new(stream, 'UTF-8')
JAUTIL = RuleKit.load('elva').library(reader)
reader.close()

ZONE = ZoneId.of('Asia/Tokyo')

def date(year, month, dayOfWeek, nth)
	week = DayOfWeek.valueOf(dayOfWeek)
	date = LocalDate.of(year, Month.valueOf(month), 1)
	date.with(TemporalAdjusters.dayOfWeekInMonth(nth, week))
end

HOURDB = [17, 18, 19, 20]
MODEDB = ['CW', 'cw']

module BandEnum
	B3_5 =  3500
	B7_0 =  7000
	B14_ = 14000
	B21_ = 21000
	B28_ = 28000
	B50_ = 50000
	def self.all
		self.constants.map{|name| self.const_get(name)}
	end
end

CITYDB = LocalCityBase.load('city.ja').toList
stream = CITYDB.stream().filter(->(c) {c.getCode.length <= 3})
CITIES = stream.collect(Collectors.toList)

SCORES = {'UEC' => 5, 'L' => 4, 'I' => 3, 'H' => 2}

def verify_time(time)
	HOURDB.include?(time.withZoneSameInstant(ZONE).getHour)
end

def verify_city(city)
	CITIES.stream().map(->(c) {c.getCode == city}).findAny
end

def verify_item(item, bandDB)
	time = JAUTIL.invoke('qxsl-time', item)
	call = JAUTIL.invoke('qxsl-call', item)
	band = JAUTIL.invoke('qxsl-band', item)
	mode = JAUTIL.invoke('qxsl-mode', item)
	code = JAUTIL.invoke('qxsl-code', item)
	city,suf = vals = code.split(/([HIL]|UEC)$/)
	return Failure.new(item, 'bad code') if not vals.length == 2
	return Failure.new(item, 'bad time') if not verify_time(time)
	return Failure.new(item, 'bad city') if not verify_city(city)
	return Failure.new(item, 'bad band') if not bandDB.include?(band)
	return Failure.new(item, 'bad mode') if not MODEDB.include?(mode)
	return Success.new(item, SCORES[suf], [call, band], [band, city])
end

class ContestUEC < Contest
	def get(name)
		eval name
	end
	def invoke(name, args)
		method(name).call(*args)
	end
	def getStartDay(year)
		date(year, 'JULY', 'SATURDAY', 3)
	end
	def getFinalDay(year)
		getStartDay(year)
	end
	def getDeadLine(year)
		LocalDate.of(year, 8, 31)
	end
	def getName()
		'電通大コンテスト'
	end
	def getHost()
		'JA1ZGP'
	end
	def getMail()
		'uectest-info@ja1zgp.com'
	end
	def getLink()
		'www.ja1zgp.com/uectest_public_info'
	end
end

class SectionUEC < Section
	def initialize(name, band)
		super()
		@name = name
		@band = band
	end
	def getCode()
		'UEC'
	end
	def getName()
		@name
	end
	def score(items)
		score,calls,mults = items.toScoreAndKeys()
		score > 0? score * mults.size: 0
	end
	def verify(item)
		verify_item(JAUTIL.decode(item), @band)
	end
end

class AllBandUEC < SectionUEC
	def initialize(name)
		super('%s部門' % name, BandEnum.all)
	end
end

class SinBandUEC < SectionUEC
	def initialize(band)
		super('%s部門' % Band.new(band), [band])
	end
end

AQSO = AllBandUEC.new('オールバンド')
S3_5 = SinBandUEC.new(BandEnum::B3_5)
S7_0 = SinBandUEC.new(BandEnum::B7_0)
S14_ = SinBandUEC.new(BandEnum::B14_)
S21_ = SinBandUEC.new(BandEnum::B21_)
S28_ = SinBandUEC.new(BandEnum::B28_)
S50_ = SinBandUEC.new(BandEnum::B50_)
ASWL = AllBandUEC.new('SWL')

# returns redefined ALLJA1 contest
TEST = ContestUEC.new(AQSO, S3_5, S7_0, S14_, S21_, S28_, S50_, ASWL)
