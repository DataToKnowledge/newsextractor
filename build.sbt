name := "NewsExtractor"

version := "1.0"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers ++= Seq("Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  "Local Maven Repository" at "file://"+Path.userHome.absolutePath+"/.m2/repository",
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.0-RC2",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.0-RC2",
  "com.typesafe.akka" %% "akka-cluster" % "2.3.0-RC2",
  "com.ning" % "async-http-client" % "1.7.23",
  "org.scalatest" % "scalatest_2.10" % "2.0" % "test",
  "org.scalacheck" %% "scalacheck" % "1.11.3" % "test")
 
