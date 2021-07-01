name := "ats4"
version := "SNAPSHOT"
scalaVersion := "2.13.3"
scalacOptions += "-feature"
libraryDependencies ++= Seq(
	filters,
	guice,
	jdbc,
	"qxsl" % "qxsl" % "0.1.194",
	"org.jruby" % "jruby-core" % "9.+",
	"com.h2database" % "h2" % "1.4.+",
	"com.typesafe.play" %% "play-mailer" % "7.0.+",
	"com.typesafe.play" %% "play-mailer-guice" % "7.0.+",
	"com.github.aselab" %% "scala-activerecord" % "0.+",
	"com.github.aselab" %% "scala-activerecord-play2" % "0.+",
	"io.circe" %% "circe-core" % "0.13.+",
	"io.circe" %% "circe-generic" % "0.13.+"
)
resolvers += "qxsl" at "https://nextzlog.github.io/qxsl/mvn"
lazy val root = (project in file(".")).enablePlugins(PlayScala)
routesImport += "java.util.UUID"
TwirlKeys.templateImports ++= Seq(
	"play.api.Configuration",
	"qxsl.draft._",
	"qxsl.model._",
	"qxsl.ruler._",
	"qxsl.sheet._",
	"qxsl.table._",
	"scala.io.Source",
	"scala.jdk.CollectionConverters._",
	"views.html.comps._"
)
