package sandbox

import better.files._
import File._
import com.typesafe.config.{ Config, ConfigException, ConfigFactory }
import org.scalatest.{ fixture, MustMatchers, Outcome, ParallelTestExecution, Tag }
import scribe.Level

class RealmMergedSpec extends fixture.WordSpec with MustMatchers with ParallelTestExecution {
  type Fixture = TestFixture
  override type FixtureParam = Fixture

  class TestFixture

  def createTaskFixture(): Fixture = new Fixture

  override def withFixture( test: OneArgTest ): Outcome = {
    scribe.Logger.root.clearModifiers().withHandler( minimumLevel = Some( Level.Debug ) ).replace()

    val fixture = createTaskFixture()
    try {
      test( fixture )
    } finally {}
  }

  object WIP extends Tag( "wip" )

  "Realm Configuration Merge" should {
    "merge values with a default configuration" in { f: Fixture =>
      val config = ConfigFactory.load()
      val default = config.getConfig( "olp.pipeline.default" )
      val bmw = config.getConfig( "olp-bmw" )
      default.getDouble( "scaling.factor.cpu.request" ) mustBe 0.5
      bmw.getDouble( "scaling.factor.cpu.request" ) mustBe 1.0

      default.getBytes( "resource.unit.memory" ) mustBe 7000000000L
      bmw.getBytes( "resource.unit.memory" ) mustBe 12000000000L

      default.getBoolean( "allowToggleActivationViaRequest" ) mustBe false
      bmw.getBoolean( "allowToggleActivationViaRequest" ) mustBe false
    }

    "merge values via fallback" in { f: Fixture =>
      val config = ConfigFactory.load()
      val default = config.getConfig( "olp.pipeline.default" )
      val foo = config.getConfig( "foo" ).withFallback( default )
      foo.getDouble( "scaling.factor.cpu.request" ) mustBe 0.5
    }

    "undefined block with fallback throws a Missing exception" in { f: Fixture =>
      val config = ConfigFactory.load()
      val default = config.getConfig( "olp.pipeline.default" )

      an[ConfigException.Missing] must be thrownBy config.getConfig( "bar" ).withFallback( default )
    }

    "separate realm configurations can be merged" taggedAs (WIP) in { f: Fixture =>
      val realmDir: File = file"./src/test/resources/realms"
      val realms = {
        realmDir.list
          .filter { _.extension == Some( ".conf" ) }
          .map { _.toJava }
          .map { ConfigFactory.parseFile }
      }

      val config = {
        realms
          .foldLeft( ConfigFactory.load() ) { ( r, acc ) =>
            acc.withFallback( r )
          }
          .resolve()
      }

      val bar = config.getConfig( "bar" )
      bar.getDouble( "scaling.factor.memory.request" ) mustBe 1.0
      bar.getInt( "resource.unit.cores" ) mustBe 8

      val zed = config.getConfig( "zed" )
      zed.getDouble( "scaling.factor.memory.request" ) mustBe 1.0
      zed.getInt( "resource.unit.cores" ) mustBe 17
    }
  }
}
