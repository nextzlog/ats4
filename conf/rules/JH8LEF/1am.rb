# AREA-1 AM CONTEST DEFINED by ATS-4

java_import 'java.time.DayOfWeek'
java_import 'java.time.LocalDate'
java_import 'java.time.Year'
java_import 'java.time.ZoneId'
java_import 'java.time.temporal.TemporalAdjusters'
java_import 'qxsl.draft.Band'
java_import 'qxsl.draft.Qxsl'
java_import 'qxsl.ruler.Element'
java_import 'qxsl.ruler.Failure'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.ruler.Section'
java_import 'qxsl.ruler.Success'
java_import 'qxsl.utils.AssetUtil'

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

HOURDB = [10, 11, 12]
BANDDB = [50000]
MODEDB = ['AM']
CITYDB = prepare_city_base()

SCORES = {true => 2, false => 1}

def verify_time(time)
	HOURDB.include?(time.withZoneSameInstant(ZONEID).getHour)
end

def verify_city(city)
	CITYDB.any?{|c| c.code == city}
end

def verify_area(code)
	code.length >= 4
end

def verify_item(item)
	time = item.value(Qxsl::TIME)
	call = item.value(Qxsl::CALL)
	band = item.value(Qxsl::BAND).intValue
	mode = item.value(Qxsl::MODE)
	rcvd = item.getRcvd.value(Qxsl::CODE)
	sent = item.getSent.value(Qxsl::CODE)
	rcvd_area = verify_area(rcvd)
	sent_area = verify_area(sent)
	return Failure.new(item, 'bad time') if not verify_time(time)
	return Failure.new(item, 'bad city') if not verify_city(rcvd)
	return Failure.new(item, 'bad band') if not BANDDB.include?(band)
	return Failure.new(item, 'bad mode') if not MODEDB.include?(mode)
	return Success.new(item, SCORES[rcvd_area || sent_area])
end

def unique_item(item)
	call = item.value(Qxsl::CALL)
	band = item.value(Qxsl::BAND).intValue
	Element.new([call, band])
end

def entity_item(item)
	band = item.value(Qxsl::BAND).intValue
	code = item.getRcvd.value(Qxsl::CODE)
	city,suf = code.split(/([HIL]|1AM)$/)
	Element.new([band, city])
end

class Program1AM < Program
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
	def year()
		opt_year(method(:getStartDay))
	end
	def getStartDay(year)
		schedule(year, 12, 4, 'SUNDAY')
	end
	def getFinalDay(year)
		schedule(year, 12, 4, 'SUNDAY')
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
	end
	def code()
		'1エリアAM'
	end
	def name()
		"#{@name}部門"
	end
	def getCityList()
		CITYDB
	end
	def verify(item)
		verify_item(JAUTIL.normalize(item, @name))
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

FIX = Section1AM.new('1エリア内固定局')
MOB = Section1AM.new('1エリア内移動局')
OUT = Section1AM.new('1エリア外局')
QRP = Section1AM.new('QRP')
SWL = Section1AM.new('SWL')

# returns contest definition
Program1AM.new(FIX, MOB, OUT, QRP, SWL)
