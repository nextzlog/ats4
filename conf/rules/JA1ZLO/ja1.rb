# ALLJA1 CONTEST DEFINED for ATS-4

java_import 'java.time.DayOfWeek'
java_import 'qxsl.ruler.Program'
java_import 'qxsl.ruler.RuleKit'
java_import 'qxsl.utils.AssetUtil'

ALLJA1 = RuleKit.load('allja1.lisp').contest

class ProgramJA1 < Program::Annual
	def initialize()
		super(6, 4, DayOfWeek::SATURDAY)
		ALLJA1.each{|section| add(section)}
	end

	def name()
		ALLJA1.name
	end

	def host()
		'東大無線部'
	end

	def mail()
		'allja1@ja1zlo.u-tokyo.org'
	end

	def link()
		'ja1zlo.u-tokyo.org/allja1'
	end

	def help()
		AssetUtil.root.string('rules/JA1ZLO/ja1.md')
	end

	def get(name)
		eval name
	end

	def getFinalDay(year)
		getStartDay(year)
	end

	def getDeadLine(year)
		getStartDay(year).plusWeeks(3)
	end

	def limitMultipleEntry(code)
		1
	end

	def conflict(entries)
		muls = entries.map{|e| e.name.include?("団体")}
		alls = entries.map{|e| e.name.include?("総合")}
		in1s = entries.map{|e| e.name.include?("1エリア内")}
		js1s = entries.map{|e| /1エリア内 .+ 総合/.match?(e.name)}
		return true if muls.any? and not muls.all?
		return true if in1s.any? and not js1s.any?
		not alls.any? and not entries.empty?
	end
end

# returns contest definition
ProgramJA1.new
