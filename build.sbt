name := "ats3"

version := "SNAPSHOT"

libraryDependencies ++= Seq(
	jdbc, anorm, "com.typesafe" %% "play-plugins-mailer" % "2.2.0"
)

play.Project.playScalaSettings

scalacOptions += "-feature"
