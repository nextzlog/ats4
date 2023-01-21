# AREA-1 AM CONTEST DEFINED for ATS-4

java_import 'java.time.DayOfWeek'
java_import 'java.time.LocalDate'
java_import 'java.time.ZoneId'
java_import 'qxsl.draft.Band'
java_import 'qxsl.draft.Qxsl'
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
	inner = JAUTIL.get('cities-area1')
	outer = JAUTIL.get('out-of-area1')
	inner = inner.select{|c| c.code.length >= 4}
	outer = outer.select{|c| c.code.length <= 3}
	inner + outer
end

JCCs = prepare_city_base()

SCORES = {true => 2, false => 1}

class Program1AM < Program::Annual
	def initialize()
		super(12, 4, DayOfWeek::SUNDAY)
	end

	def name()
		'1エリアAMコンテスト'
	end

	def host()
		'まんなかくらぶ'
	end

	def mail()
		'1amtest-info@example.com'
	end

	def link()
		'6mnet.jp/mannaka/blog'
	end

	def help()
		AssetUtil.root.string('rules/JH8LEF/1am.md')
	end

	def get(name)
		eval name
	end

	def getFinalDay(year)
		getStartDay(year)
	end

	def getDeadLine(year)
		LocalDate.of(year, 1, 31)
	end

	def limitMultipleEntry(code)
		1
	end

	def conflict(entries)
		false
	end
end

class Section1AM < Section
	def initialize(name)
		super()
		@name = name
		@band = [Band.new(50_000)]
		@mode = [Mode.new('AM')]
		@hour = [10, 11, 12]
	end

	def code()
		'1エリアAM'
	end

	def name()
		"#{@name}部門"
	end

	def getCityList()
		JCCs
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
		PREFs.include?(code)? '': "code: #{code}"
	end

	def points(item)
		rcvd = item.getRcvd.value(Qxsl::CODE).to_s.length >= 4
		sent = item.getSent.value(Qxsl::CODE).to_s.length >= 4
		SCORES[rcvd || sent]
	end

	def unique(item)
		Element.new([item.getBoth(Qxsl::CALL)])
	end

	def entity(item)
		Element.new([item.getRcvd(Qxsl::CODE)])
	end

	def result(list)
		score,mults = list.toArray
		score > 0? score * mults.size: 0
	end
end

RULE = Program1AM.new
RULE.add(Section1AM.new('1エリア内固定局'))
RULE.add(Section1AM.new('1エリア内移動局'))
RULE.add(Section1AM.new('1エリア外局'))
RULE.add(Section1AM.new('QRP'))
RULE.add(Section1AM.new('SWL'))

# returns contest definition
RULE
