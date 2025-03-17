# VUS CONTEST DEFINED for ATS-4

require 'rules/ats'

NAME = '電通大VUSコンテスト'
HOST = 'JA1ZGP'
MAIL = 'uectest-info@ja1zgp.com'
LINK = 'www.ja1zgp.com/uectest-vus_public_info'
ZDAT = 'http://ja6ycu.in.coocan.jp/ZLOG/acag.dat'

module Sections
	VHF = 'V/UHF'
	SHF = 'SHF'
	ALL = 'オールバンド'
	SWL = 'SWL'
	JR  = 'こども'
	NC  = 'ニューカマー'
end

module BandEnum
	B144 = Band.new(   144_000)
	B430 = Band.new(   430_000)
	B1_2 = Band.new( 1_200_000)
	B2_4 = Band.new( 2_400_000)
	B5_6 = Band.new( 5_600_000)
	B10_ = Band.new(10_000_000)
	BVHF = [B144, B430]
	BSHF = [B1_2, B2_4, B5_6, B10_]
	BALL = [*BVHF, *BSHF]
end

module ModeEnum
	CW  = Mode.new('CW')
	AM  = Mode.new('AM')
	FM  = Mode.new('FM')
	SSB = Mode.new('SSB')
	ALL = [CW, AM, FM, SSB]
end

SCORE_BAND = {
	BandEnum::B144 => 1,
	BandEnum::B430 => 1,
	BandEnum::B1_2 => 1,
	BandEnum::B2_4 => 2,
	BandEnum::B5_6 => 2,
	BandEnum::B10_ => 3,
}

SCORE_MODE = {
	ModeEnum::CW => 2,
	ModeEnum::AM => 1,
	ModeEnum::FM => 1,
	ModeEnum::SSB => 1,
}

class ProgramVUS < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 5, 1, DayOfWeek::MONDAY)
	end

	def getDeadLine(year)
		getStartDay(year).plusWeeks(2)
	end
end

class SectionVUS < SectionATS
	def initialize(band)
		super(band.to_s, band, ModeEnum::ALL, 12...18, ZDAT)
	end

	def points(item)
		band = SCORE_BAND[item.getBoth(Qxsl::BAND)]
		mode = SCORE_MODE[item.getBoth(Qxsl::MODE)]
		band * mode
	end

	def unique(item)
		call = item.getBoth(Qxsl::CALL)
		band = item.getBoth(Qxsl::BAND)
		mode = item.getBoth(Qxsl::MODE)
		Element.new([call, band, mode])
	end
end

class SectionAll < SectionVUS
	def initialize(bands, name)
		super(bands)
		setName(name)
	end
end

RULE = ProgramVUS.new
RULE.add(SectionVUS.new(BandEnum::B144))
RULE.add(SectionVUS.new(BandEnum::B430))
RULE.add(SectionVUS.new(BandEnum::B1_2))
RULE.add(SectionVUS.new(BandEnum::B2_4))
RULE.add(SectionVUS.new(BandEnum::B5_6))
RULE.add(SectionVUS.new(BandEnum::B10_))
RULE.add(SectionAll.new(BandEnum::BVHF, Sections::VHF))
RULE.add(SectionAll.new(BandEnum::BSHF, Sections::SHF))
RULE.add(SectionAll.new(BandEnum::BALL, Sections::JR))
RULE.add(SectionAll.new(BandEnum::BALL, Sections::NC))
RULE.add(SectionAll.new(BandEnum::BALL, Sections::ALL))
RULE.add(SectionAll.new(BandEnum::BALL, Sections::SWL))
RULE
