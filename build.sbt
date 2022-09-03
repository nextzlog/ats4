name := "ats4"
version := "SNAPSHOT"
scalaVersion := "2.13.8"
scalacOptions += "-feature"
libraryDependencies ++= Seq(
	filters,
	guice,
	jdbc,
	"qxsl" % "qxsl" % "0.1.268",
	"org.jruby" % "jruby-core" % "9.+",
	"com.h2database" % "h2" % "1.4.+",
	"com.typesafe.play" %% "play-mailer" % "7.0.+",
	"com.typesafe.play" %% "play-mailer-guice" % "7.0.+"
)
resolvers += "qxsl" at "https://nextzlog.github.io/qxsl/mvn"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
routesImport += "java.util.UUID"
TwirlKeys.templateImports ++= Seq(
	"play.api.Configuration",
	"ats4.data._",
	"ats4.root._",
	"qxsl.draft._",
	"qxsl.model._",
	"qxsl.ruler._",
	"qxsl.sheet._",
	"qxsl.table._",
	"scala.io.Source",
	"scala.jdk.CollectionConverters._"
)
