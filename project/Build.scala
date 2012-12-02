import sbt._
import Keys._

object OptimizerBuild extends Build {
  
  lazy val root = Project(
    id = "optimizer",
    base = file(".")
  ).settings(
    organization := "com.gravitydev",
    version := "0.1.0",
    libraryDependencies ++= Seq(
      "org.scalatest"   %% "scalatest" % "1.7.1" % "test",
      "joda-time"       % "joda-time" % "2.0",
      "org.joda"        % "joda-convert" % "1.1",
      "commons-io"      % "commons-io" % "2.1",
      "commons-codec"   % "commons-codec" % "1.4",
      "org.scalaz"      %% "scalaz-core" % "6.0.4",
      "com.google.closure-stylesheets" % "closure-stylesheets" % "0.0.2-SNAPSHOT"
    ),
    resolvers ++= Seq(
      "gravitydev" at "http://repos.gravitydev.com/app/repos/12"
    ),
    publishTo := Some( "gravitydev" at "http://repos.gravitydev.com/app/repos/12" ),
    publishArtifact in (Compile, packageDoc) := false
  )
}

