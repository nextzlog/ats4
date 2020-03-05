name := "ats4"

version := "SNAPSHOT"

scalaVersion := "2.13.1"

resolvers += "qxsl" at "https://nextzlog.github.io/qxsl/mvn"

libraryDependencies ++= Seq(
	evolutions,
	filters,
	guice,
	jdbc,
	"com.h2database" % "h2" % "1.4.+",
	"org.playframework.anorm" %% "anorm" % "2.6.+",
	"com.typesafe.play" %% "play-mailer" % "7.0.+",
	"com.typesafe.play" %% "play-mailer-guice" % "7.0.+",
	"qxsl" % "qxsl" % "0.1.141"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions += "-feature"

TwirlKeys.templateImports ++= Seq(
	"play.api.db.Database",
	"play.api.Configuration"
)
