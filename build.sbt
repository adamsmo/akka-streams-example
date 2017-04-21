organization := "com.angieslist"

name := "actyx-challenge"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.4.5",
  "com.typesafe.akka" %% "akka-http-experimental" % "2.4.5",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.5",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "ch.qos.logback" %  "logback-classic" % "1.1.7")

libraryDependencies ++= Seq(
  "org.specs2" %% "specs2-core" % "3.8.2" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit-experimental" % "2.0.4")

scalacOptions in Test ++= Seq("-Yrangepos")