package models

import scala.collection.mutable.MutableList
import scala.collection.JavaConversions._

import java.io._

import fxlog.model._
import fxlog.serial._

object Decoder {
	val txt = MutableList[LogSheetFormat]()
	val bin = MutableList[LogSheetFormat]()
	
	new LogSheetManager().getFormats.foreach(format => {
		if(format.getMimeTypes.contains("text/plain")) {
			txt += format
		} else {
			bin += format
		}
	})

	def decode(file: File) = new Domain(
		Clean.cleanAll(decodeBin(file) match {
			case null => decodeTxt(file)
			case some => some
		}).toList
	)

	def decodeBin(file: File): Document = {
		bin.foreach(format => {
			val in = new FileInputStream(file)
			try {
				return format.decode(in)
			} catch {
				case _: Exception => in.close
			}
		})
		return null
	}

	def decodeTxt(file: File): Document = {
		val text = Summary.unpack(file)
		val bais = new ByteArrayInputStream(text.getBytes("SJIS"))
		val sw = new StringWriter()
		val pw = new PrintWriter(sw)
		txt.foreach(format => try {
			return format.decode(bais)
		} catch {
			case ex: Exception => {
				pw.print(format.getName)
				pw.print(":")
				pw.println(ex.getMessage)
				bais.reset
			}
		})
		throw new IOException("cannot read: %s:%n%s".format(file, sw))
	}
}
