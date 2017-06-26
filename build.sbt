
name          := "woken-messages"

version       := sys.env.get("VERSION")getOrElse("dev")

scalaVersion  := "2.11.8"

val versions = new {
  val akka = "2.3.14"
  val spray = "1.3.2"
}

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka"   %%  "akka-actor"       % versions.akka,
    "com.typesafe.akka"   %%  "akka-remote"      % versions.akka,
    "com.typesafe.akka"   %%  "akka-cluster"     % versions.akka,
    "io.spray"            %%  "spray-json"       % versions.spray
  )
}

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-Xlint",
  "-Ywarn-dead-code",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

// Publish to Artifactory
publishMavenStyle := true
publishArtifact in Test := false
publishTo := Some("Artifactory Realm" at "http://lab01560.intranet.chuv:9082/artifactory/libs-release-local")
credentials += Credentials(new File("/build/.credentials"))

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint")

fork in Test := false

parallelExecution in Test := false

Revolver.settings : Seq[sbt.Def.Setting[_]]

fork in run := true

test in assembly := {} // Do not run tests when building the assembly

