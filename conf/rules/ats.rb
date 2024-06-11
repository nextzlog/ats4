# ABSTRACT CONTEST FOR ATS-4

java_import 'java.net.URI'
java_import 'java.net.URL'
java_import 'java.time.DayOfWeek'
java_import 'java.time.ZoneId'
java_import 'qxsl.draft.Band'
java_import 'qxsl.draft.Mode'
java_import 'qxsl.draft.Qxsl'
java_import 'qxsl.local.LocalCityBase'
java_import 'qxsl.ruler.Absence'
java_import 'qxsl.ruler.Element'
java_import 'qxsl.ruler.Failure'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.ruler.Section'
java_import 'qxsl.ruler.Success'
java_import 'qxsl.utils.AssetUtil'

JAUTIL = RuleKit.load('jautil.lisp').pattern
ZONEID = ZoneId.of('Asia/Tokyo')

class ProgramATS < Program::Annual
	def initialize(name, host, mail, link, month, week, day)
		super(month, week, day)
		@name = name
		@host = host
		@mail = mail
		@link = link
	end

	def name()
		@name
	end

	def host()
		@host
	end

	def mail()
		@mail
	end

	def link()
		URI.new(@link).isAbsolute ? @link : "https://#{@link}"
	end

	def help()
		AssetUtil.root.string('rules/ats.md')
	end

	def get(name)
		eval name
	end

	def getFinalDay(year)
		getStartDay(year)
	end

	def getDeadLine(year)
		getStartDay(year).plusMonths(1)
	end

	def limitMultipleEntry(code)
		1 # only a single section
	end

	def conflict(entries)
		entries.length > 1
	end
end

class AbsenceATS < Absence
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

class SectionATS < Section
	def initialize(name, band, mode, hour = 0..23, zdat = nil)
		super()
		@name = name
		@band = Array(band)
		@mode = Array(mode)
		@hour = Array(hour)
		@base = loadDAT(zdat).toList
		@mult = @base.map{|c| c.code}
	end

	def name()
		@name
	end

	def code()
		'部門'
	end

	def getCityList()
		@base
	end

	def trim()
		/$/
	end

	def verify(item)
		item = JAUTIL.normalize(item, @name)
		band = item.getBoth(Qxsl::BAND)
		mode = item.getBoth(Qxsl::MODE)
		time = item.getBoth(Qxsl::TIME)
		hour = time.value.withZoneSameInstant(ZONEID).getHour
		code = item.getRcvd(Qxsl::CODE).value.sub(/^599?/, '')
		city = code.sub(trim, '')
		errors = []
		errors.push("band: #{band}") unless @band.include?(band)
		errors.push("mode: #{mode}") unless @mode.include?(mode)
		errors.push("hour: #{hour}") unless @hour.include?(hour)
		errors.push("code: #{code}") unless @mult.include?(city)
		errors.push("code: #{code}") unless trim.match?(code)
		return Success.new(item, points(item)) if errors.empty?
		return Failure.new(item, errors.join(', '))
	end

	def points(item)
		1
	end

	def unique(item)
		# unique QSO = [call, band]
		call = item.getBoth(Qxsl::CALL)
		band = item.getBoth(Qxsl::BAND)
		Element.new([call, band])
	end

	def entity(item)
		# multiplier = [band, city]
		band = item.getBoth(Qxsl::BAND)
		code = item.getRcvd(Qxsl::CODE)
		city = code.value.sub(trim, '')
		Element.new([band, city])
	end

	def result(list)
		score,mults = list.toArray
		score > 0? score * mults.size: 0
	end

	def loadDAT(url)
		return JAUTIL.get('CITY-BASE') if url.nil?
		text = AssetUtil.root.http(URL.new(url), 'SJIS')
		text = text.gsub(/^(\w+) +(.+)$/, '\2 \1')
		LocalCityBase.read(text.lines[1..-2].join)
	end
end

RULE = ProgramATS.new('CQJA', 'JA1RL', 'cq@jarl.com', 'jarl.com', 4, 1, DayOfWeek::SUNDAY)
RULE.add(SectionATS.new('14MHz PH', [Band.new(14000)], [Mode.new('SSB'), Mode.new('FM')]))
RULE.add(SectionATS.new('21MHz PH', [Band.new(21000)], [Mode.new('SSB'), Mode.new('FM')]))
RULE.add(SectionATS.new('28MHz PH', [Band.new(28000)], [Mode.new('SSB'), Mode.new('FM')]))
RULE.add(SectionATS.new('50MHz PH', [Band.new(50000)], [Mode.new('SSB'), Mode.new('FM')]))
RULE
