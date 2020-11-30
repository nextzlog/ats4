# TAMAGAWA CONTEST DEFINED by ATS-4

import 'qxsl.draft.Qxsl'
import 'qxsl.local.LocalCityBase'
import 'qxsl.ruler.Contest'
import 'qxsl.ruler.Element'
import 'qxsl.ruler.Failure'
import 'qxsl.ruler.RuleKit'
import 'qxsl.ruler.Section'
import 'qxsl.ruler.Success'

require 'rules/util'

# JAUTIL library
JAUTIL = RuleKit.load('jautil.lisp').pattern
ZONEID = ZoneId.of('Asia/Tokyo')

HOURDB = [13, 14]
BANDDB = [50_000]
CITYDB = LocalCityBase.load('rules/tama.dat').toList

module ModeEnum
	MORSE = ['CW']
	PHONE = MORSE + ['SSB', 'AM', 'FM']
end

SCORES = {'CW' => 3}
SCORES.default = 2

def verify_time(time)
	HOURDB.include?(time.withZoneSameInstant(ZONEID).getHour)
end

def verify_code(code)
	CITYDB.any?{|c| c.code == code}
end

def verify_item(item, modeDB)
	time = item.value(Qxsl::TIME)
	call = item.value(Qxsl::CALL)
	band = item.value(Qxsl::BAND).intValue
	mode = item.value(Qxsl::MODE)
	code = item.getRcvd.value(Qxsl::CODE)
	return Failure.new(item, 'bad time') if not verify_time(time)
	return Failure.new(item, 'bad code') if not verify_code(code)
	return Failure.new(item, 'bad band') if not BANDDB.include?(band)
	return Failure.new(item, 'bad mode') if not modeDB.include?(mode)
	return Success.new(item, SCORES[mode])
end

def unique_item(item)
	Element.new(item.value(Qxsl::CALL))
end

def entity_item(item)
	Element.new(item.getRcvd.value(Qxsl::CODE))
end

def start_day(year)
	schedule(year, 11, 4, 'SUNDAY')
end

def dead_line(year)
	start_day(year).plusMonths(1)
end

class ContestTama < Contest
	def get(name)
		eval name
	end
	def getStartDay(year)
		opt_start_day(method(:start_day), year)
	end
	def getFinalDay(year)
		opt_start_day(method(:start_day), year)
	end
	def getDeadLine(year)
		opt_dead_line(method(:dead_line), year)
	end
	def name()
		'多摩川コンテスト(仮)'
	end
	def host()
		'APOLLO'
	end
	def mail()
		'jk1mgc@example.com'
	end
	def link()
		'apollo.c.ooco.jp'
	end
end

class SectionTama < Section
	def initialize(name, mode)
		super()
		@name = name
		@mode = mode
	end
	def name()
		@name
	end
	def code()
		'TAMA'
	end
	def verify(item)
		verify_item(JAUTIL.normalize(item, nil), @mode)
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

INNER = '流域内'
OUTER = '流域外'
SWLER = 'SWL'
MORSE = '電信'
PHONE = '電信電話'

LIST = []
LIST.push SectionTama.new(INNER + MORSE, ModeEnum::MORSE)
LIST.push SectionTama.new(INNER + PHONE, ModeEnum::PHONE)
LIST.push SectionTama.new(OUTER + MORSE, ModeEnum::MORSE)
LIST.push SectionTama.new(OUTER + PHONE, ModeEnum::PHONE)
LIST.push SectionTama.new(SWLER,         ModeEnum::PHONE)

# returns TAMAGAWA contest definition
TEST = ContestTama.new(*LIST)
