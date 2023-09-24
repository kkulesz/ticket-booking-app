scalaVersion := "2.13.8"

name := "ticket-booking-app"
version := "1.0"

val depScalaLang = "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
val depZio = "dev.zio" %% "zio" % "2.0.17"
val depZioHttp = "dev.zio" %% "zio-http" % "3.0.0-RC2"
val depZioJson = "dev.zio" %% "zio-json" % "0.6.2"
// val depZioPostgres = "dev.zio" %% "zio-sql-postgres" % "0.1.2" 

libraryDependencies ++= Seq(
  depScalaLang,
  depZio,
  depZioHttp,
  depZioJson,
  // depZioPostgres
)
