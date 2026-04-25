# VUS CONTEST DEFINED for ATS-4

require 'rules/ats'

NAME = '電通大VUSコンテスト'
HOST = 'JA1ZGP'
MAIL = 'uectest-info@ja1zgp.com'
LINK = 'www.ja1zgp.com/uectest-vus_public_info'
ZDAT = 'http://ja6ycu.in.coocan.jp/ZLOG/acag.dat'

module Names
	SAB = 'シングルオペ オールバンド'
	SJR = 'シングルオペ こども'
	SNC = 'シングルオペ ニューカマー'
	MAB = 'マルチオペ オールバンド'
	SWL = 'SWL'
	SOP = 'シングルオペ'
	VHF = 'シングルオペ V/UHF'
	SHF = 'シングルオペ SHF'
end

module Codes
	GEN = '総合'
	RD1 = '第1ラウンド'
	RD2 = '第2ラウンド'
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
	BandEnum::B2_4 => 10,
	BandEnum::B5_6 => 10,
	BandEnum::B10_ => 15,
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

	def conflict(entries)
		codes = entries.map{|e| e.code}.uniq
		names = entries.map{|e| e.name}.uniq

		gen = codes.include?(Codes::GEN)
		rd1 = codes.include?(Codes::RD1)
		rd2 = codes.include?(Codes::RD2)

		return true if gen && (rd1 || rd2)

		vhf = names.any?{|e| e.include?(Names::VHF)}
		shf = names.any?{|e| e.include?(Names::SHF)}

		return vhf && shf
	end
end

class SectionVUS < SectionATS
	def initialize(area, band, name, code)
		super("#{area}エリア #{name} #{band}", band, ModeEnum::ALL, 9...15, ZDAT, code)
	end

	def verify(item)
		band = item.getBoth(Qxsl::BAND)
		time = item.getBoth(Qxsl::TIME)

		vhf = BandEnum::BVHF.include?(band)
		shf = BandEnum::BSHF.include?(band)

		rd1 = time.local.getHour < 11
		rd2 = time.local.getHour > 11

		return super(item) if rd1 && shf
		return super(item) if rd2 && vhf

		return Failure.new(item, "${band} @ {time}")
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
		mode = mode.value.gsub('AM', 'SSB')
		Element.new([call, band, mode])
	end

	def getAwardLimit(scores)
		return 3 if scores.size >= 30
		return 2 if scores.size >= 11
		return 1
	end
end

class SectionAll < SectionVUS
	def initialize(area, bands, name, code)
		super(area, bands, name, code)
		setName("#{area}エリア #{name}")
	end
end

RULE = ProgramVUS.new
for area in [*1..9, 0] do
	RULE.add(AbsenceATS.new(Codes::GEN))
	RULE.add(SectionAll.new(area, BandEnum::BALL, Names::SAB, Codes::GEN))
	RULE.add(SectionAll.new(area, BandEnum::BALL, Names::SJR, Codes::GEN))
	RULE.add(SectionAll.new(area, BandEnum::BALL, Names::SNC, Codes::GEN))
	RULE.add(SectionAll.new(area, BandEnum::BALL, Names::SNC, Codes::GEN))
	RULE.add(SectionAll.new(area, BandEnum::BALL, Names::MAB, Codes::GEN))
	RULE.add(SectionAll.new(area, BandEnum::BALL, Names::SWL, Codes::GEN))

	# 09:00 - 11:00 JST
	RULE.add(AbsenceATS.new(Codes::RD1))
	RULE.add(SectionVUS.new(area, BandEnum::B1_2, Names::SOP, Codes::RD1))
	RULE.add(SectionVUS.new(area, BandEnum::B2_4, Names::SOP, Codes::RD1))
	RULE.add(SectionVUS.new(area, BandEnum::B5_6, Names::SOP, Codes::RD1))
	RULE.add(SectionVUS.new(area, BandEnum::B10_, Names::SOP, Codes::RD1))
	RULE.add(SectionAll.new(area, BandEnum::BSHF, Names::SHF, Codes::RD1))

	# 12:00 - 15:00 JST
	RULE.add(AbsenceATS.new(Codes::RD2))
	RULE.add(SectionVUS.new(area, BandEnum::B144, Names::SOP, Codes::RD2))
	RULE.add(SectionVUS.new(area, BandEnum::B430, Names::SOP, Codes::RD2))
	RULE.add(SectionAll.new(area, BandEnum::BVHF, Names::VHF, Codes::RD2))
end
RULE
