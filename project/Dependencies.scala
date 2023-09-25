import sbt._

object Dependencies {
  object Basic {
    private val scalaLangVersion = "2.1.1"

    val scalaLang = "org.scala-lang.modules" %% "scala-parser-combinators" % scalaLangVersion

    val all: Seq[ModuleID] = Seq(scalaLang)
  }

  object Cats {
    private val catsVersion       = "2.2.0"
    private val catsEffectVersion = "2.2.0"

    val core    = "org.typelevel" %% "cats-core"    % catsVersion
    val effect  = "org.typelevel" %% "cats-effect"  % catsEffectVersion

    val all: Seq[ModuleID] = Seq(core, effect)
  }

  object ZIO {
    private val zioVersion              = "1.0.1" // "1.0.1" // "2.0.17"
    private val zioHtppVersion          = "3.0.0-RC2"
    private val zioJsonVersion          = "0.6.2"
    private val zioInteropCarsVersion   = "2.1.4.0" // "2.1.4.0" // "2.0.0.0-RC12"
    // private val zioNioVersion = "2.0.0"

    val zio             = "dev.zio" %% "zio"                % zioVersion
    val zioHttp         = "dev.zio" %% "zio-http"           % zioHtppVersion
    val zioJson         = "dev.zio" %% "zio-json"           % zioJsonVersion
    val zioInteropCats  = "dev.zio" %% "zio-interop-cats"   % zioInteropCarsVersion
    // val zioNio = "dev.zio" %% "zio-nio" % zioNioVersion

    val all: Seq[ModuleID] = Seq(zio, zioHttp, zioJson, zioInteropCats)
  }

  object Doobie {
    private val version = "0.9.2"

    val core        = "org.tpolecat" %% "doobie-core"       % version
    val hikari      = "org.tpolecat" %% "doobie-hikari"     % version
    val postgres    = "org.tpolecat" %% "doobie-postgres"   % version
    val refined     = "org.tpolecat" %% "doobie-refined"    % version

    val all: Seq[ModuleID] = Seq(core, hikari, refined, postgres)
  }

  object Circe {
    private val version         = "0.13.0"
    private val versionExtras   = "0.13.0"

    val core                = "io.circe" %% "circe-core"            % version
    val generic             = "io.circe" %% "circe-generic"         % version
    val parser              = "io.circe" %% "circe-parser"          % version
    val refined             = "io.circe" %% "circe-refined"         % version
    val `generic-extras`    = "io.circe" %% "circe-generic-extras"  % versionExtras

    val all: Seq[ModuleID] =
      Seq(core, generic, parser, refined, `generic-extras`)
  }
}
