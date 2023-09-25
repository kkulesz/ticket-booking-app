import Dependencies._

scalaVersion := "2.13.3" // 2.13.8 // 2.13.3

name := "ticket-booking-app"
version := "1.0"

libraryDependencies ++=
  Basic.all ++
    Cats.all ++
    ZIO.all ++
    Doobie.all ++
    Circe.all
