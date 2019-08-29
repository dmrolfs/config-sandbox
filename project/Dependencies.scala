import sbt.Keys._
import sbt._

object Dependencies {
  object Scope {
    def compile( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "compile" )
    def provided( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "provided" )
    def test( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "test" )
    def runtime( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "runtime" )
    def container( deps: ModuleID* ): Seq[ModuleID] = deps map ( _ % "container" )
  }

  trait Module {
    def groupId: String
    def version: String 
    def artifactId( id: String ): String
    def isScala: Boolean = true
    def module( id: String ): ModuleID = {
      if ( isScala ) groupId %% artifactId(id) % version 
      else groupId % artifactId(id) % version
    }
  }

  trait SimpleModule extends Module {
    def artifactIdRoot: String
    override def artifactId( id: String ): String = if ( id.isEmpty ) artifactIdRoot else s"$artifactIdRoot-$id"
  }


  object log {
    object scribe extends SimpleModule {
      override val groupId: String = "com.outr"
      override def artifactIdRoot: String = "scribe"
      override def version: String = "2.7.3"
      def all = Seq( core )
      val core = module( "" )
      val slf4j = module( "slf4j" )
    }

    def all = scribe.all
  }

  val config = "com.typesafe" % "config" % "1.3.4"
  val ficus = "com.iheart" %% "ficus" % "1.4.7"

  object betterFiles extends SimpleModule {
    override val groupId = "com.github.pathikrit"
    override val artifactIdRoot = "better-files"
    override val version = "3.4.0"
    val core = module( "" )
    val akka = module( "akka" )
    def all = Seq( core, akka )
  }

  object quality {
    val scalatest = "org.scalatest" %% "scalatest" % "3.0.8" withSources() withJavadoc()

    val cats = "com.ironcorelabs" %% "cats-scalatest" % "2.2.0"
    val inmemory = "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.1.0"

    object mockito extends SimpleModule {
      override val groupId = "org.mockito"
      override val artifactIdRoot = "mockito"
      override val version = "2.13.0"
      override val isScala = false
      val core = module( "core" ) withSources() withJavadoc()
    }
  }


  val commonDependencies = {
    log.all ++
    Seq(
      config,
      betterFiles.core
    ) ++
    Scope.test(
      quality.scalatest,
//      quality.cats,
      quality.mockito.core
    )
  }

  val defaultDependencyOverrides: Seq[sbt.librarymanagement.ModuleID] = Seq.empty[sbt.librarymanagement.ModuleID]
}
