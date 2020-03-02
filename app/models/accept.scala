package models

import java.nio.charset.Charset
import java.nio.file.{Files, Path, Paths}
import java.text.Normalizer
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import play.api.Configuration
import play.api.data.{Form, Forms}
import play.api.db.Database
import play.api.libs.Files.TemporaryFile
import play.api.{Configuration,Logger}
import play.libs.mailer.{Email,MailerClient}
import qxsl.extra.field.Qxsl.NAME;
import qxsl.ruler.Summary
import scala.jdk.CollectionConverters._
import scala.util.Try

case class Attend(sect: Option[String], city: Option[String]) {
	def valid = sect.nonEmpty && city.nonEmpty
	def code = Sections.forName(section).getCode
	def section = (if(CallArea.area1.filter(city.get.startsWith).nonEmpty) "1エリア内 %s" else "1エリア外 %s").format(sect.get)
}
case class Submit(call: String, name: String, addr: String, mail: String, comm: String, list: Seq[Attend]) {
	def norm = Normalizer.normalize(call.toUpperCase, Normalizer.Form.NFKC).replaceAll("\\W","")
	def safe = norm.split('/').head // against directory-traversal attacks
	def path(implicit c: Configuration) = Files.createDirectories(Paths.get("ats4.rcvd"))
	def dfmt(implicit c: Configuration) = DateTimeFormatter.ofPattern("'%s'.yyyyMMdd.HHmmss.'log'")
	def file(implicit c: Configuration) = path.resolve(LocalDateTime.now.format(dfmt).format(safe))
	def joint = Sections.joint(list.filter(_.valid).map(_.section).map(Sections.forName))
	def push(temp: TemporaryFile)(implicit cfg: Configuration, db: Database, smtp: MailerClient) = {
		val major = Major(call=norm,name=name,addr=addr,mail=this.mail,comm=comm,file=file.toString)
		val minor = this.list.filter(_.valid).map(a => new Tables(temp.path,norm,a.section,a.city.get).minor)
		val joint = this.joint.map(js => new Tables(temp.path,norm,js.getName,"").minor)
		temp.moveFileTo(Paths.get(major.file).toFile)
		Logger(getClass).info(s"accepted: $this")
		major.elim
		major.push
		minor.foreach(_.elim)
		minor.foreach(_.push)
		joint.foreach(_.push)
		val host = cfg.get[String]("test.host")
		val from = cfg.get[String]("test.from")
		val repl = cfg.get[String]("test.mail")
		val text = views.txt.pages.email(major).body.trim
		val mail = new Email
		mail.setSubject(text.linesIterator.toSeq.head.split("//")(0).trim)
		mail.setFrom("%s <%s>".format(host,from))
		mail.addTo("%s <%s>".format(this.call,this.mail))
		mail.addBcc(repl)
		mail.setBodyText(text.linesWithSeparators.toSeq.tail.mkString.trim)
		Try(smtp.send(mail))
		major
	}
}

object Submit {
	def apply(call: String)(implicit db: Database): Option[Submit] = Try {
		val post = Major.ofCall(call).get
		val minors = Minor.ofCall(call).map(Attend.apply).map(a => a.code->a).toMap
		val sorted = Sections.order.map(name => minors.getOrElse(name, Attend(None,None)))
		Some(Submit(post.call,post.name,post.addr,post.mail,post.comm,sorted))
	}.getOrElse(None)
}

object Attend {
	def apply(minor: Minor): Attend = Attend(Some(minor.sect.split(" ", 2).last), Some(minor.city))
}

object AttendForm extends Form[Attend](Forms.mapping(
	"sect" -> Forms.optional(Forms.text),
	"city" -> Forms.optional(Forms.text),
)(Attend.apply)(Attend.unapply).verifying(
	at => at.sect.isEmpty || at.city.nonEmpty
), Map.empty, Nil, None)

object SubmitForm extends Form[Submit](Forms.mapping(
	"call" -> Forms.nonEmptyText,
	"name" -> Forms.nonEmptyText,
	"addr" -> Forms.nonEmptyText,
	"mail" -> Forms.email,
	"comm" -> Forms.text,
	"list" -> Forms.seq(AttendForm.mapping)
)(Submit.apply)(Submit.unapply), Map.empty, Nil, None)

class Tables(path: Path, call: String, sect: String, city: String) {
	val chset = Charset.forName("JISAutoDetect")
	def bytes = Files.readAllBytes(path)
	def lines = Files.readString(path, chset)
	def table = new qxsl.table.TableFormats().decode(bytes)
	def sheet = new qxsl.sheet.SheetFormats().unpack(lines)
	val score = Sections.forName(sect).summarize(Try(table).getOrElse(sheet))
	def minor = Minor(call,sect,city,score.score,Sections.ja1.score(score))
}
