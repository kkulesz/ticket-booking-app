scalaVersion := "2.13.8"

name := "ticket-booking-app"
version := "1.0"

val depScalaLang = "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
val depZio = "dev.zio" %% "zio" % "2.0.17"
val depZioHttp = "dev.zio" %% "zio-http" % "3.0.0-RC2"

libraryDependencies ++= Seq(
    depScalaLang,
    depZio,
    depZioHttp
)

