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
        assemblyJarName in assembly := "woken-messages.jar",
        libraryDependencies ++= Seq(
          library.akkaActor,
          library.akkaRemote,
          library.akkaCluster,
          library.sprayJson,
          library.scalaCheck % Test,
          library.scalaTest  % Test
        )
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val scalaCheck = "1.13.5"
      val scalaTest  = "3.0.3"
      val akka = "2.3.16"
      val spray = "1.3.4"
    }
    val scalaCheck: ModuleID = "org.scalacheck" %% "scalacheck" % Version.scalaCheck
    val scalaTest: ModuleID = "org.scalatest"  %% "scalatest"  % Version.scalaTest
    val akkaActor: ModuleID = "com.typesafe.akka" %% "akka-actor" % Version.akka
    val akkaRemote: ModuleID =  "com.typesafe.akka" %% "akka-remote" % Version.akka
    val akkaCluster: ModuleID = "com.typesafe.akka" %% "akka-cluster" % Version.akka
    val sprayJson: ModuleID = "io.spray" %% "spray-json" % Version.spray
  }

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++
  gitSettings ++
  scalafmtSettings

lazy val commonSettings =
  Seq(
    scalaVersion := "2.11.8",
    organization := "eu.humanbrainproject.mip",
    organizationName := "LREN CHUV",
    startYear := Some(2017),
    licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0")),
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
      "-encoding", "UTF-8"
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

// Publish to Artifactory
publishMavenStyle := true
publishArtifact in Test := false
publishTo := Some("Artifactory Realm" at "http://lab01560.intranet.chuv:9082/artifactory/libs-release-local")
credentials += Credentials(new File("/build/.credentials"))

Revolver.settings : Seq[sbt.Def.Setting[_]]

lazy val gitSettings =
  Seq(
    git.useGitDescribe := true
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true,
    scalafmtOnCompile.in(Sbt) := false,
    scalafmtVersion := "1.1.0"
  )
