# UNIV ORG QSO PARTY DEFINED for ATS-4

require 'rules/ats'

NAME = '大学社団QSOパーティ'
HOST = 'JA1ZLO'
MAIL = 'allja1.info@gmail.com'
LINK = 'ja1zlo.u-tokyo.org/univ_onair_together'
ZDAT = 'http://ja6ycu.in.coocan.jp/ZLOG/acag.dat'

BANDS = [
	Band.new(       135),
	Band.new(       475),
	Band.new(     1_900),
	Band.new(     3_500),
	Band.new(     7_000),
	Band.new(    10_000),
	Band.new(    14_000),
	Band.new(    18_000),
	Band.new(    21_000),
	Band.new(    24_000),
	Band.new(    28_000),
	Band.new(    50_000),
	Band.new(   144_000),
	Band.new(   430_000),
	Band.new( 1_200_000),
	Band.new( 2_400_000),
	Band.new( 5_600_000),
	Band.new(10_000_000),
]

MODES_CW = [
	Mode.new('CW'),
	Mode.new('A1A'),
	Mode.new('F2A'),
]

MODES_PH = [
	Mode.new('PH'),
	Mode.new('AM'),
	Mode.new('FM'),
	Mode.new('SSB'),
	Mode.new('LSB'),
	Mode.new('USB'),
	Mode.new('DSB'),
]

MODES_WS = [
	Mode.new("FT8"),
	Mode.new("FT4"),
	Mode.new("JT65"),
]

MODES_DG = [
	Mode.new("RTTY"),
	Mode.new("SSTV"),
]

MODES = MODES_CW + MODES_PH + MODES_WS + MODES_DG

class ProgramUNI < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 11, 1)
	end

	def getFinalDay(year)
		LocalDate.of(year, 11, 30)
	end

	def getDeadLine(year)
		LocalDate.of(year + 1, 1, 31)
	end

	def conflict(entries)
		false
	end
end

class SectionUNI_Base < SectionATS
	def initialize(name)
		super(name, BANDS, MODES, 0...24, ZDAT)
		setCode(name)
	end

	def verifyCode(code)
		true
	end

	def points(item)
		mode = item.getBoth(Qxsl::MODE)

		return 3 if MODES_CW.include?(mode)
		return 3 if MODES_PH.include?(mode)
		return 1 if MODES_WS.include?(mode)
		return 3 if MODES_DG.include?(mode)
	end

	def unique(item)
		time = item.getBoth(Qxsl::TIME)
		call = item.getBoth(Qxsl::CALL)
		band = item.getBoth(Qxsl::BAND)
		mode = item.getBoth(Qxsl::MODE)

		date = time.value().toLocalDate()

		Element.new([date, call, band, mode]) 
	end

	def entity(item)
		time = item.getBoth(Qxsl::TIME)
		Element.new([time.value.toLocalDate])
	end

	def getAwardLimit(scores)
		return 1
	end
end

class SectionUNI_Call < SectionUNI_Base
	def result(items)
		items.score
	end
end

class SectionUNI_Date < SectionUNI_Base
	def result(items)
		items.keys(0).size
	end
end

RULE = ProgramUNI.new
RULE.add(SectionUNI_Call.new("局数部門"))
RULE.add(SectionUNI_Date.new("運用日数部門"))
RULE.add(SectionUNI_Base.new("総合部門"))
RULE
