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
          library.akkaManagementBase,
          library.akkaManagementClusterHttp,
          library.swaggerAnnotations,
          library.sprayJson,
          library.catsCore,
          library.postgresQl,
          library.bugsnag,
          library.slf4j,
          library.log4jSlf4j,
          library.disruptor,
          library.scalaLogging,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        ),
        crossScalaVersions := Seq("2.11.12", "2.12.8")
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck         = "1.14.0"
      val scalaTest          = "3.0.7"
      val akka               = "2.5.23"
      val akkaHttp           = "10.1.8"
      val akkaManagement     = "1.0.1"
      val swaggerAnnotations = "2.0.8"
      val sprayJson          = "1.3.5"
      val cats               = "1.6.0"
      val slf4j              = "1.7.26"
      val log4j              = "2.11.2"
      val disruptor          = "3.4.2"
      val scalaLogging       = "3.9.0"
      val postgresQl         = "42.2.5"
      val bugsnag            = "3.5.0"
    }
    object ExclusionRules {
    val excludeAkkaClusterSharding =
      ExclusionRule(organization = "com.typesafe.akka", name = "akka-cluster-sharding_2.11")
    }
    val scalaCheck: ModuleID   = "org.scalacheck"    %% "scalacheck"   % Version.scalaCheck
    val scalaTest: ModuleID    = "org.scalatest"     %% "scalatest"    % Version.scalaTest
    val akkaActor: ModuleID    = "com.typesafe.akka" %% "akka-actor"   % Version.akka
    val akkaRemote: ModuleID   = "com.typesafe.akka" %% "akka-remote"  % Version.akka
    val akkaCluster: ModuleID  = "com.typesafe.akka" %% "akka-cluster" % Version.akka
    val akkaClusterTools: ModuleID = "com.typesafe.akka" %% "akka-cluster-tools" % Version.akka
    val akkaSlf4j: ModuleID    = "com.typesafe.akka" %% "akka-slf4j"   % Version.akka
    val akkaHttp: ModuleID     = "com.typesafe.akka" %% "akka-http"    % Version.akkaHttp
    val akkaManagementBase: ModuleID = "com.lightbend.akka.management" %% "akka-management" % Version.akkaManagement
    val akkaManagementClusterHttp: ModuleID =  "com.lightbend.akka.management" %% "akka-management-cluster-http" % Version.akkaManagement excludeAll ExclusionRules.excludeAkkaClusterSharding
    val swaggerAnnotations: ModuleID = "io.swagger.core.v3"  % "swagger-annotations" % Version.swaggerAnnotations
    val sprayJson: ModuleID    = "io.spray"          %% "spray-json"   % Version.sprayJson
    val catsCore: ModuleID     = "org.typelevel"     %% "cats-core"    % Version.cats
    val slf4j: ModuleID        = "org.slf4j"          % "slf4j-api"    % Version.slf4j
    val log4jSlf4j: ModuleID   = "org.apache.logging.log4j" % "log4j-slf4j-impl" % Version.log4j
    val disruptor: ModuleID    = "com.lmax"           % "disruptor"    % Version.disruptor
    val scalaLogging: ModuleID = "com.typesafe.scala-logging" %% "scala-logging" % Version.scalaLogging
    val postgresQl: ModuleID   = "org.postgresql"     % "postgresql"   % Version.postgresQl
    val bugsnag: ModuleID      = "com.bugsnag"       % "bugsnag"       % Version.bugsnag

  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings = commonSettings ++ gitSettings ++ scalafmtSettings ++ bintraySettings ++ publishSettings

lazy val commonSettings =
  Seq(
    (ThisBuild / organization) := "ch.chuv.lren.woken",
    (ThisBuild / organizationName) := "LREN CHUV for Human Brain Project",
    (ThisBuild / homepage) := Some(url(s"https://github.com/HBPMedical/${name.value}/#readme")),
    (ThisBuild / licenses) := Seq("AGPL-3.0" ->
      url(s"https://github.com/LREN-CHUV/${name.value}/blob/${version.value}/LICENSE")
    ),
    (ThisBuild / startYear) := Some(2017),
    (ThisBuild / description) := "Library of messages passed between Woken components",
    (ThisBuild / developers) := List(
      Developer("ludovicc", "Ludovic Claude", "@ludovicc", url("https://github.com/ludovicc"))
    ),
    (ThisBuild / scmInfo) := Some(ScmInfo(url(s"https://github.com/HBPMedical/${name.value}"), s"git@github.com:HBPMedical/${name.value}.git")),
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
    (Compile / unmanagedSourceDirectories) := Seq((Compile / scalaSource).value),
    (Test / unmanagedSourceDirectories) := Seq((Test / scalaSource).value),
    (Compile / compile / wartremoverWarnings) ++= Warts.unsafe,
    (run / fork) := true,
    (assembly / test) := {},
    (Test / fork) := false,
    (Test / parallelExecution) := false
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
    (Sbt / scalafmtOnCompile) := false,
    scalafmtVersion := "1.5.1"
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
  (Test / publishArtifact) := false,
  homepage := Some(url("https://github.com/LREN-CHUV/woken-messages")),
  pomIncludeRepository := Function.const(false)
)
