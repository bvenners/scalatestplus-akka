
organization := "org.scalatestplus.akka"

name := "scalatestplus-akka"

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.8"

scalacOptions += "-deprecation"

coverageEnabled := true

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0-M16-SNAP3"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-remote" % "2.4.0"

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.0"
