# UEC CONTEST DEFINED for ATS-4

java_import 'java.time.DayOfWeek'
java_import 'java.time.LocalDate'
java_import 'java.time.ZoneId'
java_import 'qxsl.draft.Band'
java_import 'qxsl.draft.Mode'
java_import 'qxsl.draft.Qxsl'
java_import 'qxsl.local.LocalCityBase'
java_import 'qxsl.local.LocalCityItem'
java_import 'qxsl.ruler.Absence'
java_import 'qxsl.ruler.Element'
java_import 'qxsl.ruler.Failure'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.ruler.Section'
java_import 'qxsl.ruler.Success'
java_import 'qxsl.utils.AssetUtil'

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

PREFs = prepare_city_base()

ALLBAND = 'オールバンド/SWL'
SINBAND = 'シングルバンド'

module BandEnum
	B3_5 = Band.new( 3_500)
	B7_0 = Band.new( 7_000)
	B14_ = Band.new(14_000)
	B21_ = Band.new(21_000)
	B28_ = Band.new(28_000)
	B50_ = Band.new(50_000)

	def self.all
		self.constants.map{|name| self.const_get(name)}
	end
end

SCORES = {'UEC' => 5, 'L' => 4, 'I' => 3, 'H' => 2}

class ProgramUEC < Program::Annual
	def initialize()
		super(7, 3, DayOfWeek::SATURDAY)
	end

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

	def help()
		AssetUtil.root.string('rules/JA1ZGP/uec.md')
	end

	def get(name)
		eval name
	end

	def getFinalDay(year)
		getStartDay(year)
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
		@mode = [Mode.new('CW')]
		@hour = [17, 18, 19, 20]
	end

	def name()
		@name
	end

	def getCityList()
		PREFs
	end

	def verify(item)
		errors = []
		item = JAUTIL.normalize(item, @name)
		errors.push verify_time(item.getBoth(Qxsl::TIME))
		errors.push verify_band(item.getBoth(Qxsl::BAND))
		errors.push verify_mode(item.getBoth(Qxsl::MODE))
		errors.push verify_code(item.getRcvd(Qxsl::CODE))
		return Success.new(item, points(item)) if errors.all? ''
		return Failure.new(item, errors.grep_v('').join(', '))
	end

	def verify_time(time)
		hour = time.value.withZoneSameInstant(ZONEID).getHour
		@hour.include?(hour)? '': "hour: #{hour}"
	end

	def verify_band(band)
		@band.include?(band)? '': "band: #{band}"
	end

	def verify_mode(mode)
		@mode.include?(mode)? '': "mode: #{mode}"
	end

	def verify_code(code)
		city,suf = code.value.split(/([HIL]|UEC)$/)
		PREFs.any? {|c| c.code == city}? '': "code: #{code}"
	end

	def points(item)
		code = item.getRcvd.value(Qxsl::CODE)
		city,suf = code.split(/([HIL]|UEC)$/)
		SCORES[suf]
	end

	def unique(item)
		call = item.value(Qxsl::CALL)
		band = item.value(Qxsl::BAND).intValue
		Element.new([call, band])
	end

	def entity(item)
		band = item.value(Qxsl::BAND).intValue
		code = item.getRcvd.value(Qxsl::CODE)
		city,suf = code.split(/([HIL]|UEC)$/)
		Element.new([band, city])
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
		super('%s部門' % band, [band])
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

	def name()
		"#{@code} 不参加"
	end

	def code()
		@code
	end
end

RULE = ProgramUEC.new
RULE.add(AbsenceUEC.new(ALLBAND))
RULE.add(AbsenceUEC.new(SINBAND))
RULE.add(AllBandUEC.new('オールバンド'))
RULE.add(SinBandUEC.new(BandEnum::B3_5))
RULE.add(SinBandUEC.new(BandEnum::B7_0))
RULE.add(SinBandUEC.new(BandEnum::B14_))
RULE.add(SinBandUEC.new(BandEnum::B21_))
RULE.add(SinBandUEC.new(BandEnum::B28_))
RULE.add(SinBandUEC.new(BandEnum::B50_))
RULE.add(AllBandUEC.new('SWL'))

# returns contest definition
RULE
