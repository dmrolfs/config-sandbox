import sbt.Keys._
import Dependencies._

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)

libraryDependencies ++= commonDependencies


scalafmtOnCompile in ThisBuild := true
