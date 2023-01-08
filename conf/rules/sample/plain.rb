# PLAIN SAMPLE CONTEST FOR ATS-4 BEGINNERS

java_import 'java.time.LocalDate'
java_import 'java.time.Year'
java_import 'qxsl.draft.Band'
java_import 'qxsl.draft.Code'
java_import 'qxsl.draft.Mode'
java_import 'qxsl.draft.Qxsl'
java_import 'qxsl.local.LocalCityBase'
java_import 'qxsl.ruler.Element'
java_import 'qxsl.ruler.Failure'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.Section'
java_import 'qxsl.ruler.Success'

class PlainProgram < Program
	def name()
		'サンプルコンテスト'
	end

	def host()
		'JA1ZLO'
	end

	def mail()
		'ja1zlo@example.com'
	end

	def link()
		'pafelog.net'
	end

	def help()
		'未対応の交信記録を提出した場合は、運営に報告が必要です。'
	end

	def get(name)
		eval name
	end

	def year()
		Year.now.getValue
	end

	def getStartDay(year)
		LocalDate.of(year, 1, 1)
	end

	def getFinalDay(year)
		LocalDate.of(year, 1, 1)
	end

	def getDeadLine(year)
		LocalDate.of(year, 2, 1)
	end

	def limitMultipleEntry(code)
		1 # only a single section
	end

	def conflict(entries)
		false
	end
end

class PlainSection < Section
	def initialize(name, band, mode)
		super()
		@name = name
		@band = band
		@mode = mode
	end

	def name()
		@name
	end

	def code()
		'サンプル部門'
	end

	def getCityList()
		JCCs
	end

	def verify(item)
		errors = []
		errors.push verify_band(item.getBoth(Qxsl::BAND))
		errors.push verify_mode(item.getBoth(Qxsl::MODE))
		errors.push verify_code(item.getRcvd(Qxsl::CODE))
		return Success.new(item, points(item)) if errors.all? ''
		return Failure.new(item, errors.grep_v('').join(', '))
	end

	def verify_band(band)
		@band.include?(band)? '': "bad band: #{band}"
	end

	def verify_mode(mode)
		@mode.include?(mode)? '': "bad mode: #{mode}"
	end

	def verify_code(code)
		code = code.value.sub(/^599?/, '')
		JCCs.any? {|c| c.code == code}? '': "bad code: #{code}"
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
		# multiplier = [band, code]
		band = item.getBoth(Qxsl::BAND)
		code = item.getRcvd(Qxsl::CODE)
		Element.new([band, code])
	end

	def result(list)
		score,mults = list.toArray
		score > 0? score * mults.size: 0
	end
end

JCCs = LocalCityBase.load('qxsl/local/city.ja').toList.select{|c| c.code.length > 3}

RULE = PlainProgram.new(
	PlainSection.new('14MHz CW部門', [Band.new(14000)], [Mode.new('CW')]),
	PlainSection.new('21MHz CW部門', [Band.new(21000)], [Mode.new('CW')]),
	PlainSection.new('28MHz CW部門', [Band.new(28000)], [Mode.new('CW')]),
	PlainSection.new('50MHz CW部門', [Band.new(50000)], [Mode.new('CW')]),
)

# returns contest definition
RULE
