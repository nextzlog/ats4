package models

import scala.io.Source

import java.io._
import javax.xml.parsers._

import org.xml.sax._
import org.xml.sax.helpers.DefaultHandler

object Summary {
	val parserFactory = SAXParserFactory.newInstance()
	
	def unpack(file: File): String = {
		try {
			val logSheetHandler = new LogSheetHandler()
			val parser = parserFactory.newSAXParser()
			parser.parse(preprocess(file), logSheetHandler)
			return logSheetHandler.buffer.toString()
		} catch {
			case ex: ParserConfigurationException =>
			throw new IOException(ex)
		}
	}

	def prepreprocess(file: File): String = {
		val source = Source.fromFile(file, "SJIS")
		val text = source.mkString("")
		source.close()
		val buffer = new StringBuilder()
		val escape = new StringBuilder()
		for(i <- 0 until text.length) {
			val ch = text.charAt(i)
			if(escape.length() == 0) {
				if(ch == '&') escape.append(ch)
				else buffer.append(ch)
			} else if(ch == ';') {
				escape.toString() match {
					case "&gt"=> buffer.append('>')
					case "&lt"=> buffer.append('<')
					case _=> buffer.append(escape)
				}
				escape.setLength(0)
			} else escape.append(ch)
		}
		return buffer.append(escape).toString()
	}
	
	def preprocess(file: File): InputSource = {
		val text = prepreprocess(file)
		val sb = new StringBuilder()
		sb.append("<document>")
		var isTag = false
		var isAttr = false
		var isQuoted = false
		val attrVal = new StringBuilder()
		for(i <- 0 until text.length) {
			val ch = text.charAt(i)
			if(isTag && !isAttr && ch == '=') {
				isAttr = true
				sb.append(ch)
			} else if(isAttr) {
				if(!isQuoted) {
					if(ch == '"') isQuoted = true
					else if(ch == '>' || ch == ' ') {
						isAttr = false
						sb.append('"')
						sb.append(attrVal)
						sb.append('"')
						sb.append(ch)
						attrVal.setLength(0)
						if(ch == '>') isTag = false
					}
					else attrVal.append(ch)
				} else if(ch == '"') {
					isQuoted = false
				} else attrVal.append(ch)
			} else if(!isTag && ch == '<') {
				isTag = true
				sb.append(ch)
			} else if(isTag && ch == '>') {
				isTag = false
				sb.append(ch)
			} else if(ch == '<') {
				sb.append("&lt;")
			} else if(ch == '>') {
				sb.append("&gt;")
			} else if(ch == '&') {
				sb.append("&amp;")
			} else if(ch == '"') {
				sb.append("&quot;")
			} else if(ch == '\'') {
				sb.append("&apos;")
			} else if(isTag) {
				sb.append(Character.toUpperCase(ch))
			} else sb.append(ch)
		}
		val xml = sb.append("</document>").toString()
		return new InputSource(new StringReader(xml))
	}

	class LogSheetHandler extends DefaultHandler {
		val buffer = new StringBuilder()
		var isSumSheet = false
		var isLogSheet = false
		
		override def startElement
		(uri: String, ln: String, qn: String, attrs: Attributes) {
			if(qn.equals("SUMMARYSHEET")) isSumSheet = true
			isLogSheet = !isSumSheet||qn.equals("LOGSHEET")
		}

		override def characters(ch: Array[Char], off: Int, len: Int) {
			if(isLogSheet) buffer.append(new String(ch, off, len))
		}
	}
}
