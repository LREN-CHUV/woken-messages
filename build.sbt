// *****************************************************************************
// Projects
// *****************************************************************************

lazy val `woken-messages` =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin, GitVersioning, GitBranchPrompt)
    .settings(settings)
    .settings(
      Seq(
        libraryDependencies ++= Seq(
          library.akkaActor,
          library.akkaRemote,
          library.akkaCluster,
          library.akkaClusterTools,
          library.akkaSlf4j,
          library.akkaHttp,
          library.swaggerAnnotations,
          library.sprayJson,
          library.catsCore,
          library.postgresQl,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        ),
        crossScalaVersions := Seq("2.11.12", "2.12.5")
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck         = "1.13.5"
      val scalaTest          = "3.0.5"
      val akka               = "2.5.12"
      val akkaHttp           = "10.1.1"
      val swaggerAnnotations = "1.5.19"
      val sprayJson          = "1.3.4"
      val cats               = "1.1.0"
      val slf4j              = "1.7.25"
      val log4j              = "2.11.0"
      val disruptor          = "3.4.2"
      val postgresQl         = "42.2.2"
    }
    val scalaCheck: ModuleID  = "org.scalacheck"    %% "scalacheck"   % Version.scalaCheck
    val scalaTest: ModuleID   = "org.scalatest"     %% "scalatest"    % Version.scalaTest
    val akkaActor: ModuleID   = "com.typesafe.akka" %% "akka-actor"   % Version.akka
    val akkaRemote: ModuleID  = "com.typesafe.akka" %% "akka-remote"  % Version.akka
    val akkaCluster: ModuleID = "com.typesafe.akka" %% "akka-cluster" % Version.akka
    val akkaClusterTools: ModuleID = "com.typesafe.akka" %% "akka-cluster-tools" % Version.akka
    val akkaSlf4j: ModuleID   = "com.typesafe.akka" %% "akka-slf4j"   % Version.akka
    val akkaHttp: ModuleID    = "com.typesafe.akka" %% "akka-http"    % Version.akkaHttp
    val swaggerAnnotations: ModuleID = "io.swagger"  % "swagger-annotations"       % Version.swaggerAnnotations
    val sprayJson: ModuleID   = "io.spray"          %% "spray-json"   % Version.sprayJson
    val catsCore: ModuleID    = "org.typelevel"     %% "cats-core"    % Version.cats
    val slf4j: ModuleID       = "org.slf4j"          % "slf4j-api"    % Version.slf4j
    val log4jSlf4j: ModuleID  = "org.apache.logging.log4j" % "log4j-slf4j-impl" % Version.log4j
    val disruptor: ModuleID   = "com.lmax"           % "disruptor"    % Version.disruptor
    val postgresQl: ModuleID  = "org.postgresql"     % "postgresql"   % Version.postgresQl
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings = commonSettings ++ gitSettings ++ scalafmtSettings ++ bintraySettings ++ publishSettings

lazy val commonSettings =
  Seq(
    organization in ThisBuild := "ch.chuv.lren.woken",
    organizationName in ThisBuild := "LREN CHUV for Human Brain Project",
    homepage in ThisBuild := Some(url(s"https://github.com/HBPMedical/${name.value}/#readme")),
    licenses in ThisBuild := Seq("AGPL-3.0" ->
      url(s"https://github.com/LREN-CHUV/${name.value}/blob/${version.value}/LICENSE")
    ),
    startYear in ThisBuild := Some(2017),
    description in ThisBuild := "Library of messages passed between Woken components",
    developers in ThisBuild := List(
      Developer("ludovicc", "Ludovic Claude", "@ludovicc", url("https://github.com/ludovicc"))
    ),
    scmInfo in ThisBuild := Some(ScmInfo(url(s"https://github.com/HBPMedical/${name.value}"), s"git@github.com:HBPMedical/${name.value}.git")),
    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-Xlint",
      "-Yno-adapted-args",
      "-Ywarn-dead-code",
      "-Ywarn-value-discard",
      "-Ypartial-unification",
      "-language:_",
      "-target:jvm-1.8",
      "-encoding",
      "UTF-8"
    ),
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
    unmanagedSourceDirectories.in(Compile) := Seq(scalaSource.in(Compile).value),
    unmanagedSourceDirectories.in(Test) := Seq(scalaSource.in(Test).value),
    wartremoverWarnings in (Compile, compile) ++= Warts.unsafe,
    fork in run := true,
    test in assembly := {},
    fork in Test := false,
    parallelExecution in Test := false
  )

lazy val gitSettings =
  Seq(
    git.gitTagToVersionNumber := { tag: String =>
      if (tag matches "[0-9]+\\..*") Some(tag)
      else None
    },
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.4.0"
  )

// Publish to BinTray
lazy val bintraySettings =
  Seq(
    bintrayEnsureLicenses := false,
    bintrayOrganization := Some("hbpmedical"),
    bintrayRepository := "maven",
    bintrayPackageLabels := Seq("woken", "library", "algorithm-factory")
  )

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  homepage := Some(url("https://github.com/LREN-CHUV/woken-messages")),
  pomIncludeRepository := Function.const(false)
)
