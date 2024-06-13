# ALL GUNMA CONTEST DEFINED by ATS-4

require 'rules/ats'

NAME = 'オール群馬コンテスト'
HOST = 'JARL群馬県支部'
MAIL = 'agclog@gmail.com'
LINK = 'www.jarl.com/gunma/'
ZDAT = 'http://ja6ycu.in.coocan.jp/ZLOG/gunma.dat'

HOUR = Array(20..23) + Array(6..11)

AREAS = {'県内' => 2, '県外' => 4}

BANDS_HF = [
	Band.new( 1_900),
	Band.new( 3_500),
	Band.new( 7_000),
	Band.new(14_000),
	Band.new(21_000),
	Band.new(28_000),
]

BANDS_LO = [
	Band.new( 50_000),
	Band.new(144_000),
	Band.new(430_000),
]

BANDS_UP = [
	Band.new(1200_000),
]

BANDS_MB = [
	*BANDS_HF,
	*BANDS_LO,
	*BANDS_UP,
]

BANDS_VU = [
	*BANDS_LO,
	*BANDS_UP,
]

MODES_CW = [
	Mode.new('CW'),
]

MODES_PH = [
	Mode.new('PH'),
	Mode.new('AM'),
	Mode.new('FM'),
	Mode.new('SSB'),
]

MAIN_BANDS = {}
MAIN_BANDS.store('マルチバンド', BANDS_MB)
MAIN_BANDS.store('マルチバンド HF', BANDS_HF)
MAIN_BANDS.store('マルチバンド V/UHF', BANDS_VU)
MAIN_BANDS.store('QRP HF', BANDS_HF)
MAIN_BANDS.store('QRP V/UHF', BANDS_LO)
MAIN_BANDS.update(BANDS_MB.map{|b| [b.to_s, b]}.to_h)

MISC_BANDS = {}
MISC_BANDS.store('社団', BANDS_MB)
MISC_BANDS.store('ジュニア 社団', BANDS_MB)
MISC_BANDS.store('ジュニア マルチバンド HF', BANDS_HF)
MISC_BANDS.store('ジュニア マルチバンド V/UHF', BANDS_VU)
MISC_BANDS.store('シニア マルチバンド HF', BANDS_HF)
MISC_BANDS.store('シニア マルチバンド V/UHF', BANDS_VU)
MISC_BANDS.store('YL', BANDS_MB)
MISC_BANDS.store('SWL', BANDS_MB)

MAIN_MODES = {}
MAIN_MODES.store('電信', MODES_CW)
MAIN_MODES.store('電話', MODES_PH)
MAIN_MODES.store('電信電話', MODES_CW + MODES_PH)

MISC_MODES = MAIN_MODES.slice('電信電話')

MAIN = [MAIN_MODES.keys, MAIN_BANDS.keys]
MISC = [MISC_MODES.keys, MISC_BANDS.keys]

BANDS = MAIN_BANDS.merge(MISC_BANDS)
MODES = MAIN_MODES.merge(MISC_MODES)

class ProgramAGC < ProgramATS
	def initialize()
		super(NAME, HOST, MAIL, LINK, 5, 3, DayOfWeek::SATURDAY)
	end

	def getDeadLine(year)
		getStartDay(year).plusMonths(1).plusDays(1)
	end
end

class SectionAGC < SectionATS
	def initialize(name, area, mode, band)
		super(name, BANDS[band], MODES[mode], HOUR, ZDAT)
		@mult = @mult.select{|c| c.length >= AREAS[area]}
	end

	def points(item)
		MODES_CW.include?(item.getBoth(Qxsl::MODE)) ? 2 : 1
	end
end

RULE = ProgramAGC.new

AREAS.keys.product(*(MAIN + MISC)) do |area, mode, band|
	RULE.add(SectionAGC.new([area, mode, band].join(' '), area, mode, band))
end

RULE
