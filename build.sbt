name := "ats4"

version := "SNAPSHOT"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
	jdbc, evolutions, guice, filters,
	"com.h2database" % "h2" % "1.4.+",
	"com.typesafe.play" %% "anorm" % "2.6.+",
	"com.typesafe.play" %% "play-mailer" % "7.0.+",
	"com.typesafe.play" %% "play-mailer-guice" % "7.0.+"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions += "-feature"

TwirlKeys.templateImports ++= Seq(
	"play.api.db.Database",
	"play.api.Configuration"
)
