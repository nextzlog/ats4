# UEC CONTEST DEFINED for ATS-4

require 'rules/ats'

NAME = '電通大コンテスト'
HOST = 'JA1ZGP'
MAIL = 'uectest-info@ja1zgp.com'
LINK = 'www.ja1zgp.com/uectest_public_info'
ZDAT = 'http://ja6ycu.in.coocan.jp/ZLOG/uec.dat'

ALL = 'オールバンド'
SIN = 'シングルバンド'
SWL = 'SWL'

SCORE = {'UEC' => 5, 'L' => 4, 'I' => 3, 'H' => 2}

class ProgramUEC < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 7, 3, DayOfWeek::SATURDAY)
	end

	def getDeadLine(year)
		LocalDate.of(year, 8, 31)
	end

	def limitMultipleEntry(code)
		return 1 if code == ALL
		return 2 if code == SIN
	end

	def conflict(entries)
		codes = entries.map{|e| e.code}.uniq
		names = entries.map{|e| e.name}.uniq
		codes.size > 1 || names.size < entries.size
	end
end

class SectionUEC < SectionATS
	def initialize(band)
		super(band.to_s, band, Mode.new('CW'), 17...20, ZDAT)
		setCode(SIN)
	end

	def trim()
		/([HIL]|UEC)$/
	end

	def points(item)
		SCORE[item.getRcvd.value(Qxsl::CODE).slice(trim)]
	end
end

class SectionAll < SectionUEC
	def initialize(name)
		super(BandEnum::BALL)
		setName(name)
		setCode(ALL)
	end
end

module BandEnum
	B1_9 = Band.new( 1_900)
	B3_5 = Band.new( 3_500)
	B7_0 = Band.new( 7_000)
	B14_ = Band.new(14_000)
	B21_ = Band.new(21_000)
	B28_ = Band.new(28_000)
	B50_ = Band.new(50_000)
	BALL = [B1_9, B3_5, B7_0, B14_, B21_, B28_, B50_]
end

RULE = ProgramUEC.new
RULE.add(AbsenceATS.new(ALL))
RULE.add(AbsenceATS.new(SIN))
RULE.add(SectionUEC.new(BandEnum::B1_9))
RULE.add(SectionUEC.new(BandEnum::B3_5))
RULE.add(SectionUEC.new(BandEnum::B7_0))
RULE.add(SectionUEC.new(BandEnum::B14_))
RULE.add(SectionUEC.new(BandEnum::B21_))
RULE.add(SectionUEC.new(BandEnum::B28_))
RULE.add(SectionUEC.new(BandEnum::B50_))
RULE.add(SectionAll.new(ALL))
RULE.add(SectionAll.new(SWL))
RULE
