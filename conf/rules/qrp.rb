# QRP CONTEST DEFINED for ATS-4

require 'rules/ats'

NAME = 'QRPコンテスト'
HOST = 'JARL QRP CLUB'
MAIL = 'contest2@jaqrp.net'
LINK = 'https://www2.jaqrp.org/contests/'
ZDAT = 'http://ja6ycu.in.coocan.jp/ZLOG/xpo.dat'

CRAFT = '自作機'
OTHER = '一般'

MODES_CW = [
	Mode.new('CW'),
]

MODES_PH = [
	Mode.new('PH'),
	Mode.new('AM'),
	Mode.new('FM'),
	Mode.new('SSB'),
]

MODES = MODES_CW + MODES_PH

CLASSES = {
	CRAFT => 'H',
	OTHER => 'G',
}

class ProgramQRP < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 11, 3)
	end

	def getDeadLine(year)
		LocalDate.of(year, 11, 23)
	end
end

class SectionQRP < SectionATS
	def initialize(type, band)
		short = "#{CLASSES[type]}#{band}".sub('.', '').sub('MHz', '')
		super("#{short} #{band} #{type}", band, MODES, 13...21, ZDAT)
	end

	def trim()
		/[HMLP]?$/i
	end

	def unique(item)
		call = item.getBoth(Qxsl::CALL)
		band = item.getBoth(Qxsl::BAND)
		mode = item.getBoth(Qxsl::MODE)
		Element.new([call, band, MODES_CW.include?(mode)])
	end
end

class SectionAll < SectionQRP
	def initialize(type)
		super(type, BandEnum::BALL)
		setName("#{CLASSES[type]}A オールバンド #{type}")
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

RULE = ProgramQRP.new
# home-made
RULE.add(SectionAll.new(CRAFT))
RULE.add(SectionQRP.new(CRAFT, BandEnum::B1_9))
RULE.add(SectionQRP.new(CRAFT, BandEnum::B3_5))
RULE.add(SectionQRP.new(CRAFT, BandEnum::B7_0))
RULE.add(SectionQRP.new(CRAFT, BandEnum::B14_))
RULE.add(SectionQRP.new(CRAFT, BandEnum::B21_))
RULE.add(SectionQRP.new(CRAFT, BandEnum::B28_))
RULE.add(SectionQRP.new(CRAFT, BandEnum::B50_))
# manufactured
RULE.add(SectionAll.new(OTHER))
RULE.add(SectionQRP.new(OTHER, BandEnum::B1_9))
RULE.add(SectionQRP.new(OTHER, BandEnum::B3_5))
RULE.add(SectionQRP.new(OTHER, BandEnum::B7_0))
RULE.add(SectionQRP.new(OTHER, BandEnum::B14_))
RULE.add(SectionQRP.new(OTHER, BandEnum::B21_))
RULE.add(SectionQRP.new(OTHER, BandEnum::B28_))
RULE.add(SectionQRP.new(OTHER, BandEnum::B50_))
RULE
