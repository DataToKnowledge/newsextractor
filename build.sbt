import sbt._
import Process._
import Keys._

lazy val root = (project in file(".")).
  settings(
    name := "NewsExtractor",
    version := "1.1",
    scalaVersion := "2.11.5"
  )

//ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }
scalacOptions ++= Seq(
  "-deprecation",
  "-feature"
)

enablePlugins(JavaAppPackaging)
bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.akka"       %% "akka-actor" % "2.3.8",
  "com.typesafe.akka"       %% "akka-testkit" % "2.3.8",
  "com.typesafe.akka"       %% "akka-slf4j" % "2.3.8",
  "com.typesafe.akka"       %%  "akka-contrib" % "2.3.8",
  "ch.qos.logback" % "logback-classic" % "1.1.2",
  "com.ning" % "async-http-client" % "1.8.3",
  "com.github.nscala-time" %% "nscala-time" % "1.6.0",
  "org.jsoup" % "jsoup" % "1.7.3",
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23",
  "com.syncthemall" % "boilerpipe" % "1.2.2",
  "com.syncthemall" % "goose" % "2.1.25"
)

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

/* Required by com.gravity.Goose */
libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.1.2",
  "commons-lang" % "commons-lang" % "2.6",
  "commons-io" % "commons-io" % "2.0.1"
)
