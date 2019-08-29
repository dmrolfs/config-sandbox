// see: https://github.com/sbt/sbt-git#known-issues
libraryDependencies += "org.slf4j" % "slf4j-nop" % "1.7.25"

logLevel := Level.Warn

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "2.0.0-RC3-2")
addSbtPlugin( "com.eed3si9n" % "sbt-buildinfo" % "0.9.0" )
addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.0.3")
