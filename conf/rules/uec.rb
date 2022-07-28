# UEC CONTEST DEFINED by ATS-4

java_import 'qxsl.draft.Band'
java_import 'qxsl.draft.Qxsl'
java_import 'qxsl.local.LocalCityBase'
java_import 'qxsl.local.LocalCityItem'
java_import 'qxsl.ruler.Absence'
java_import 'qxsl.ruler.Contest'
java_import 'qxsl.ruler.Element'
java_import 'qxsl.ruler.Failure'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.ruler.Section'
java_import 'qxsl.ruler.Success'

require 'rules/util'

# JAUTIL library
JAUTIL = RuleKit.load('jautil.lisp').pattern
ZONEID = ZoneId.of('Asia/Tokyo')

def prepare_city_base()
	base = LocalCityBase.load('qxsl/local/city.ja').toList
	base = base.select{|c| c.code.length <= 3}
	base = base.select{|c| c.code != '01'}
	base = base.select{|c| c.code != '48'}
	base.push LocalCityItem.new('海上', '00')
	base
end

HOURDB = [17, 18, 19, 20]
MODEDB = ['CW']
CITYDB = prepare_city_base()

ALLBAND = 'オールバンド/SWL'
SINBAND = 'シングルバンド'

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
	HOURDB.include?(time.withZoneSameInstant(ZONEID).getHour)
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
	Element.new([call, band])
end

def entity_item(item)
	band = item.value(Qxsl::BAND).intValue
	code = item.getRcvd.value(Qxsl::CODE)
	city,suf = code.split(/([HIL]|UEC)$/)
	Element.new([band, city])
end

class ContestUEC < Contest
	def name()
		'電通大コンテスト'
	end
	def host()
		'JA1ZGP'
	end
	def mail()
		'uectest-info@example.com'
	end
	def link()
		'www.ja1zgp.com/uectest_public_info'
	end
	def get(name)
		eval name
	end
	def year()
		opt_year(method(:getStartDay))
	end
	def getStartDay(year)
		schedule(year, 7, 3, 'SATURDAY')
	end
	def getFinalDay(year)
		schedule(year, 7, 3, 'SATURDAY')
	end
	def getDeadLine(year)
		LocalDate.of(year, 8, 31)
	end
	def limitMultipleEntry(code)
		return 1 if code == ALLBAND
		return 2 if code == SINBAND
	end
	def conflict(entries)
		codes = entries.map{|e| e.code}.uniq
		names = entries.map{|e| e.name}.uniq
		codes.size > 1 || names.size < entries.size
	end
end

class SectionUEC < Section
	def initialize(name, band)
		super()
		@name = name
		@band = band
	end
	def name()
		@name
	end
	def getCityList()
		CITYDB
	end
	def verify(item)
		verify_item(JAUTIL.normalize(item, nil), @band)
	end
	def unique(item)
		unique_item(item)
	end
	def entity(item)
		entity_item(item)
	end
	def result(list)
		score,mults = list.toArray
		score > 0? score * mults.size: 0
	end
end

class AllBandUEC < SectionUEC
	def initialize(name)
		super('%s部門' % name, BandEnum.all)
	end
	def code()
		ALLBAND
	end
end

class SinBandUEC < SectionUEC
	def initialize(band)
		super('%s部門' % Band.new(band), [band])
	end
	def code()
		SINBAND
	end
end

class AbsenceUEC < Absence
	def initialize(code)
		super()
		@code = code
	end
	def code()
		@code
	end
	def name()
		"#{@code} 不参加"
	end
end

ANIL = AbsenceUEC.new(ALLBAND)
SNIL = AbsenceUEC.new(SINBAND)
AQSO = AllBandUEC.new('オールバンド')
S3_5 = SinBandUEC.new(BandEnum::B3_5)
S7_0 = SinBandUEC.new(BandEnum::B7_0)
S14_ = SinBandUEC.new(BandEnum::B14_)
S21_ = SinBandUEC.new(BandEnum::B21_)
S28_ = SinBandUEC.new(BandEnum::B28_)
S50_ = SinBandUEC.new(BandEnum::B50_)
ASWL = AllBandUEC.new('SWL')

# returns contest definition
ContestUEC.new(ANIL, SNIL, AQSO, S3_5, S7_0, S14_, S21_, S28_, S50_, ASWL)
