import Dependencies._

scalaVersion := "2.13.11"

name := "ticket-booking-app"
version := "1.0"

libraryDependencies ++=
  Basic.all ++
    Cats.all ++
    ZIO.all ++
    Doobie.all ++
    Circe.all

Compile / mainClass := Some("ZioApp")

assembly / mainClass := Some("ZioApp")
assembly / assemblyJarName := "app.jar"

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _                             => MergeStrategy.first
}
