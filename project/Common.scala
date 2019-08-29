import sbt.Keys._
import sbt.{ ConflictManager, _ }
import sbt.plugins.JvmPlugin

/**
  * Settings that are comment to all the SBT projects
  */
object Common extends AutoPlugin {
  override def trigger = allRequirements

  override def requires: sbt.Plugins = JvmPlugin

  override def projectSettings = Seq(
    organization := "com.github.dmrolfs",
    version := "0.0.1-SNAPSHOT",
    crossScalaVersions := Seq( "2.12.9" ),
    scalaVersion := crossScalaVersions { (vs: Seq[String]) =>
      vs.head
    }.value,
    conflictManager := ConflictManager.latestRevision,
    dependencyOverrides := Dependencies.defaultDependencyOverrides,
    resolvers ++= Seq(
      Resolver.typesafeRepo( "releases" ),
      "omen-bintray" at "http://dl.bintray.com/omen/maven",
      Resolver.bintrayRepo( "tanukkii007", "maven" ),
      Classpaths.sbtPluginReleases
    ),
    // javacOptions ++= Seq( "-source", "1.8", "-target", "1.8" ),
//    javaOptions in Test += "-Dconfig.file=conf/test/application.test.conf",
//    javaOptions in Test += "-Dsecrets.path="+baseDirectory.value+"/conf/test/secret.conf",
    // scalafmtOnCompile in ThisBuild := true,
    scalacOptions ++= Seq(
      // "-encoding",
      // "utf8",
      "-Ypartial-unification",
      "-target:jvm-1.8",
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
      "-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-encoding",
      "utf-8", // Specify character encoding used by source files.
      "-explaintypes", // Explain type errors in more detail.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Xlint:by-name-right-associative", // By-name parameter of right associative operator.
      "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
      "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
      "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
      "-Xlint:option-implicit", // Option.apply used implicit view.
      //    "-Xlint:package-object-classes",     // Class or object defined in package object.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
      "-Xlint:unsound-match", // Pattern match may not be typesafe.
      "-Yno-adapted-args", // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
      "-Ywarn-infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Ywarn-nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Ywarn-nullary-unit", // Warn when nullary methods return Unit.
      "-Ywarn-numeric-widen", // Warn when numerics are widened.
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
      //          "-Ylog-classpath",
      //     "-Xlog-implicits",
      //     "-Ymacro-debug-verbose",
      "-Ywarn-adapted-args",
      //          "-Xfatal-warnings",
      "-Xlog-reflective-calls"
    ),
    scalacOptions in Test ++= Seq( "-Yrangepos" ),
    fork := true,
    // SLF4J initializes itself upon the first logging call.  Because sbt
    // runs tests in parallel it is likely that a second thread will
    // invoke a second logging call before SLF4J has completed
    // initialization from the first thread's logging call, leading to
    // these messages:
    //   SLF4J: The following loggers will not work because they were created
    //   SLF4J: during the default configuration phase of the underlying logging system.
    //   SLF4J: See also http://www.slf4j.org/codes.html#substituteLogger
    //   SLF4J: com.imageworks.common.concurrent.SingleThreadInfiniteLoopRunner
    //
    // As a workaround, load SLF4J's root logger before starting the unit
    // tests [1].
    //
    // [1] http://stackoverflow.com/a/12095245
    fork in Test := true,
    testOptions in Test += Tests.Setup(
      classLoader =>
        classLoader
          .loadClass( "org.slf4j.LoggerFactory" )
          .getMethod( "getLogger", classLoader.loadClass( "java.lang.String" ) )
          .invoke( null, "ROOT" )
    ),
    parallelExecution in Test := false,
    testOptions in Test += Tests.Argument( TestFrameworks.ScalaTest, "-oDFT" ),
    triggeredMessage in ThisBuild := Watched.clearWhenTriggered,
    cancelable in Global := true,
    autoAPIMappings := true,
    dependencyOverrides ++= Seq(
      "com.typesafe.akka" %% "akka-persistence-cassandra" % "0.99", // match lagom 1.5.x
      "io.dropwizard.metrics" % "metrics-core" % "3.2.6" // match lagom 1.5.x
    )
  )
}
