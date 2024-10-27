# AREA-1 AM CONTEST DEFINED by ATS-4

require 'rules/ats'

NAME = '1エリアAMコンテスト'
HOST = 'まんなかくらぶ'
MAIL = '1amtest-info@example.com'
LINK = '6mnet.jp/mannaka/blog'
ZDAT = 'http://ja6ycu.in.coocan.jp/ZLOG/ja1.dat'

SCORES = {true => 2, false => 1}

class Program1AM < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 12, 4, DayOfWeek::SUNDAY)
	end

	def getDeadLine(year)
		LocalDate.of(year, 1, 31)
	end
end

class Section1AM < SectionATS
	def initialize(name)
		super(name, Band.new(50_000), Mode.new('AM'), 10..12, ZDAT)
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
end

RULE = Program1AM.new
RULE.add(Section1AM.new('1エリア内固定局'))
RULE.add(Section1AM.new('1エリア内移動局'))
RULE.add(Section1AM.new('1エリア外局'))
RULE.add(Section1AM.new('QRP'))
RULE.add(Section1AM.new('SWL'))
RULE
