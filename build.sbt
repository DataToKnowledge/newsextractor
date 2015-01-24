import sbt._
import Process._
import Keys._


lazy val root = (project in file(".")).
  settings(
    name := "NewsExtractor",
    version := "1.1",
    scalaVersion := "2.11.4"
  )

scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.8",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.8",
  //"com.typesafe.akka" %% "akka-cluster" % "2.2.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.8",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.ning" % "async-http-client" % "1.8.3",
  "com.github.nscala-time" %% "nscala-time" % "0.8.0",
  "org.scalatest" % "scalatest_2.10" % "2.1.2" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test",
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.reactivemongo" %% "reactivemongo" % "0.10.0",
  "com.syncthemall" % "boilerpipe" % "1.2.2"
)

/* Required by com.gravity.Goose */
libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.1.2",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-io" % "commons-io" % "2.0.1"
)

atmosSettings
