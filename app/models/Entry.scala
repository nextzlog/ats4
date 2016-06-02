package models

import play.api.Play.current
import play.api.Play.{configuration => conf}

import java.io._
import java.text.SimpleDateFormat
import java.util.Date

import fxlog.field._
import fxlog.model._

case class Entry(prof: Prof, time: Date, calls: Int, mults: Int) {
	def call = new Call(Prof.toHalfWidth(prof.call).split("/", 2)(0).filter{
		case ch if 'A' <= ch && ch <= 'Z' => true
		case ch if 'a' <= ch && ch <= 'z' => true
		case ch if '0' <= ch && ch <= '9' => true
		case _ => false // warning! against directory traversal cracking
	}.toUpperCase)
	def city = new City(prof.city)
	def mobi = prof.mobi
	def band = prof.band
	def mode = prof.mode
	def name = prof.name
	def addr = Prof.toHalfWidth(prof.addr).trim
	def mail = Prof.toHalfWidth(prof.mail).trim
	def comm = prof.comm match {
		case Some(text) => Some(Prof.toHalfWidth(text).trim)
		case None => None
	}

	def mobile = Mobile.isMobile(prof.mobi)
	def sect = prof.sect

	def displayCall = if(mobile) call + "/" + Pref.area(city) else call

	def score: Int = calls * mults

	def validRecords  (doc: Document) = Rule.forName(sect).pickout(doc)
	def invalidRecords(doc: Document) = Rule.forName(sect).cutdown(doc)

	def eLogFile = new File(Entry.saveDir, call + "." + Entry.dateFormat.format(time) + ".log")
	def eSumFile = new File(Entry.saveDir, call + "." + Entry.dateFormat.format(time) + ".sum")

	def save() {
		val out = new PrintStream(eSumFile)
		out.print(this)
		out.flush
		out.close
	}
}

object Entry {
	val dateFormat = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss")
	val saveDir = new File(conf.getString("ats3.savedir") match {
		case Some(path) if path.startsWith("~") => System.getProperty("user.home") + path.drop(1)
		case Some(path) => path
		case None => System.getProperty("user.home") + "/ats3"
	})
	if(!saveDir.isDirectory) saveDir.mkdirs
}
