package models

import fxlog.model.Record

object Filter {
	def 運用場所  (rec: Record) = rec.getRcvdCity != null
	def エリア外  (rec: Record) = Pref.area(rec.getRcvdCity) == 1
	def 電信限定  (rec: Record) = rec.getMode == Modes.cwMode
	def band19    (rec: Record) = Bands.band19.bands.contains(rec.getBand)
	def band35    (rec: Record) = Bands.band35.bands.contains(rec.getBand)
	def band70    (rec: Record) = Bands.band70.bands.contains(rec.getBand)
	def band14    (rec: Record) = Bands.band14.bands.contains(rec.getBand)
	def band21    (rec: Record) = Bands.band21.bands.contains(rec.getBand)
	def band28    (rec: Record) = Bands.band28.bands.contains(rec.getBand)
	def band50    (rec: Record) = Bands.band50.bands.contains(rec.getBand)
	def ローバンド(rec: Record) = Bands.bandLB.bands.contains(rec.getBand)
	def ハイバンド(rec: Record) = Bands.bandHB.bands.contains(rec.getBand)
	def 全周波数帯(rec: Record) = Bands.bandAB.bands.contains(rec.getBand)
}
