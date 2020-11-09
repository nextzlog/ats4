# UEC CONTEST DEFINED by ATS-4

import 'java.time.DayOfWeek'
import 'java.time.LocalDate'
import 'java.time.Month'
import 'java.time.ZoneId'
import 'java.time.temporal.TemporalAdjusters'
import 'qxsl.draft.Band'
import 'qxsl.draft.Qxsl'
import 'qxsl.ruler.Contest'
import 'qxsl.ruler.Failure'
import 'qxsl.ruler.RuleKit'
import 'qxsl.ruler.Section'
import 'qxsl.ruler.Success'

# JAUTIL library
UTIL = RuleKit.load('jautil.lisp').pattern
ZONE = ZoneId.of('Asia/Tokyo')

def date(year, month, dayOfWeek, nth)
	week = DayOfWeek.valueOf(dayOfWeek)
	date = LocalDate.of(year, Month.valueOf(month), 1)
	date.with(TemporalAdjusters.dayOfWeekInMonth(nth, week))
end

HOURDB = [17, 18, 19, 20]
MODEDB = ['CW', 'cw']
CITYDB = UTIL.get("CITYDB").toList.select{|c| c.code.length <= 3}

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

SCORES = {'UEC' => 5, 'L' => 4, 'I' => 3, 'H' => 2}

def verify_time(time)
	HOURDB.include?(time.withZoneSameInstant(ZONE).getHour)
end

def verify_city(city)
	CITYDB.any?{|c| c.code == city}
end

def verify_item(item, bandDB)
	time = item.value(Qxsl::TIME)
	call = item.value(Qxsl::CALL)
	band = item.value(Qxsl::BAND).intValue
	mode = item.value(Qxsl::MODE)
	code = item.getRcvd.value(Qxsl::CODE)
	city,suf = code.split(/([HIL]|UEC)$/)
	return Failure.new(item, 'bad time') if not verify_time(time)
	return Failure.new(item, 'bad city') if not verify_city(city)
	return Failure.new(item, 'bad band') if not bandDB.include?(band)
	return Failure.new(item, 'bad mode') if not MODEDB.include?(mode)
	return Success.new(item, SCORES[suf])
end

def unique_item(item)
	call = item.value(Qxsl::CALL)
	band = item.value(Qxsl::BAND).intValue
	return [call, band]
end

def entity_item(item)
	band = item.value(Qxsl::BAND).intValue
	code = item.getRcvd.value(Qxsl::CODE)
	city,suf = code.split(/([HIL]|UEC)$/)
	return [band, city]
end

class ContestUEC < Contest
	def get(name)
		eval name
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
	def name()
		'電通大コンテスト'
	end
	def host()
		'JA1ZGP'
	end
	def mail()
		'uectest-info@ja1zgp.com'
	end
	def link()
		'www.ja1zgp.com/uectest_public_info'
	end
end

class SectionUEC < Section
	def initialize(name, band)
		super()
		@name = name
		@band = band
	end
	def code()
		'UEC'
	end
	def name()
		@name
	end
	def verify(item)
		verify_item(UTIL.normalize(item, nil), @band)
	end
	def unique(item)
		unique_item(item)
	end
	def entity(item)
		entity_item(item)
	end
	def result(list)
		score,mults = list.toScoreAndEntitySets
		score > 0? score * mults.size: 0
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
