# TAMAGAWA CONTEST DEFINED for ATS-4

java_import 'java.time.DayOfWeek'
java_import 'java.time.ZoneId'
java_import 'qxsl.draft.Mode'
java_import 'qxsl.draft.Qxsl'
java_import 'qxsl.local.LocalCityBase'
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

TAMAs = LocalCityBase.load('rules/JI1YEG/tama.dat').toList

module ModeEnum
	MORSE = ['CW']
	PHONE = MORSE + ['SSB', 'AM', 'FM']
end

SCORES = {'CW' => 3}
SCORES.default = 2

class ProgramTama < Program::Annual
	def initialize()
		super(11, 4, DayOfWeek::SUNDAY)
	end

	def name()
		'多摩川コンテスト'
	end

	def host()
		'APOLLO'
	end

	def mail()
		'jk1mgc@example.com'
	end

	def link()
		'apollo.c.ooco.jp'
	end

	def help()
		AssetUtil.root.string('rules/JI1YEG/tama.md')
	end

	def get(name)
		eval name
	end

	def getFinalDay(year)
		getStartDay(year)
	end

	def getDeadLine(year)
		getStartDay(year).plusWeeks(2)
	end

	def limitMultipleEntry(code)
		1
	end

	def conflict(entries)
		entries.length > 1
	end
end

class SectionTama < Section
	def initialize(name, mode)
		super()
		@name = name
		@band = [Band.new(50_000)]
		@mode = mode.map{|m| Mode.new(m)}
		@hour = [13, 14]
	end

	def name()
		@name
	end

	def code()
		'TAMA'
	end

	def getCityList()
		TAMAs
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
		TAMAs.include?(code)? '': "code: #{code}"
	end

	def points(mode)
		SCORES[item.value(Qxsl::MODE)]
	end

	def unique(item)
		Element.new(item.value(Qxsl::CALL))
	end

	def entity(item)
		Element.new(item.getRcvd.value(Qxsl::CODE))
	end

	def result(list)
		score,mults = list.toArray
		score > 0? score * mults.size: 0
	end
end

INNER = '流域内'
OUTER = '流域外'
SWLER = 'SWL'
MORSE = '電信'
PHONE = '電信電話'

RULE = ProgramTama.new
RULE.add(SectionTama.new(INNER + MORSE, ModeEnum::MORSE))
RULE.add(SectionTama.new(INNER + PHONE, ModeEnum::PHONE))
RULE.add(SectionTama.new(OUTER + MORSE, ModeEnum::MORSE))
RULE.add(SectionTama.new(OUTER + PHONE, ModeEnum::PHONE))
RULE.add(SectionTama.new(SWLER,         ModeEnum::PHONE))

# returns contest definition
RULE
