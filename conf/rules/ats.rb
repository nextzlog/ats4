# ABSTRACT CONTEST FOR ATS-4

java_import 'java.net.URI'
java_import 'java.net.URL'
java_import 'java.time.DayOfWeek'
java_import 'java.time.LocalDate'
java_import 'java.time.temporal.TemporalAdjusters'

java_import 'qxsl.draft.Band'
java_import 'qxsl.draft.Mode'
java_import 'qxsl.local.LocalCityBase'
java_import 'qxsl.ruler.Absence'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.ruler.Section'
java_import 'qxsl.utils.AssetUtil'

class ProgramATS < Program::Default
	def initialize(name, host, mail, link, mon, nth, day = nil)
		super()
		@mon = mon
		@nth = nth
		@day = day
		setName(name)
		setHost(host)
		setMail(mail)
		setLink(URI.new(link).isAbsolute ? link : "//#{link}")
		setHelp(AssetUtil.root.string('rules/ats.md'))
	end

	def get(name)
		eval name
	end

	def getStartDay(year)
		date = LocalDate.of(year, @mon, @nth)
		@day.nil? ? date : date.with(TemporalAdjusters.dayOfWeekInMonth(@nth, @day))
	end

	def getFinalDay(year)
		getStartDay(year)
	end

	def getDeadLine(year)
		getStartDay(year).plusMonths(1)
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

class SectionATS < Section::Default
	@@UTILS = RuleKit.load('jautil.lisp').pattern
	@@CACHE = {}

	def initialize(name, band, mode, hour = 0..23, zdat = nil)
		super()
		@name = name
		setBands(*Array(band))
		setModes(*Array(mode))
		setHours(*Array(hour))
		set(loadDAT(zdat || "http://ja6ycu.in.coocan.jp/ZLOG/acag.dat"))
	end

	def name()
		@name
	end

	def code()
		'部門'
	end

	def trim()
		/$/
	end

	def verify(item)
		super(@UTILS.normalize(item, @name))
	end

	def verifyCode(code)
		trim.match?(super(code))
	end

	def cityOf(code)
		super(code).sub(trim, '')
	end

	def loadDAT(url)
		return @@CACHE.fetch(url)if @@CACHE.member?(url)
		text = AssetUtil.root.http(URL.new(url), 'SJIS')
		text = text.gsub(/^(\w+) +(.+)$/, '\2 \1').lines.drop(1)
		base = LocalCityBase.read(text.take(text.size - 1).join)
		@@CACHE[url] = base
		base
	end
end
