name := "ats4"

version := "SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
	jdbc,
	evolutions,
	"com.typesafe.play" %% "anorm" % "2.5.0",
	"com.typesafe.play" %% "play-mailer" % "5.0.0-M1",
	"joda-time" % "joda-time" % "2.3"
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions += "-feature"
