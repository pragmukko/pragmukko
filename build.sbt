
name := "Swarmakka"

version := "1.0"

scalaVersion := "2.11.7"

artifact in (Compile, assembly) := {
  val art = (artifact in (Compile, assembly)).value
  art.copy(`classifier` = Some("assembly"))
}

addArtifact(artifact in (Compile, assembly), assembly).settings

resolvers ++= Seq(
  "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/",
  "Paho Official Releases" at "https://repo.eclipse.org/content/repositories/paho-releases/",
  "Paho Nightly Snapshots" at "https://repo.eclipse.org/content/repositories/paho-snapshots/",
  Resolver.bintrayRepo("hseeberger", "maven")
)


mainClass in (Compile, run) := Some("Main")

scalacOptions := Seq("-unchecked", "-feature", "-language:postfixOps", "-deprecation", "-Xfatal-warnings", "-Xlint:_", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.4.0"
  val akkaStreamV = "1.0" 
  Seq(
    "com.typesafe.akka"  %%  "akka-actor"                         % akkaV,
    "com.typesafe.akka"  %%  "akka-cluster"                       % akkaV,
    "com.typesafe.akka"  %%  "akka-slf4j"                         % akkaV,
    "com.typesafe.akka"  %%  "akka-cluster-tools"                 % akkaV,
    "com.typesafe.akka"  %%  "akka-stream-experimental"           % akkaStreamV,
    "com.typesafe.akka"  %%  "akka-http-core-experimental"        % akkaStreamV,
    "com.typesafe.akka"  %%  "akka-http-experimental"             % akkaStreamV,
    "com.typesafe.akka"  %%  "akka-http-spray-json-experimental"  % akkaStreamV,
    "com.typesafe.akka"  %%  "akka-http-testkit-experimental"     % akkaStreamV,
    "ch.qos.logback"     %   "logback-classic"                    % "1.0.12",
    "org.specs2"         %%  "specs2-core"                        % "3.6.5"      % "test",
    "org.eclipse.paho"   %   "org.eclipse.paho.client.mqttv3"     % "1.0.3-SNAPSHOT",
    "com.sandinh"        %%  "paho-akka"                          % "1.1.1",
    "de.heikoseeberger"  %%  "akka-sse"                           % "1.1.0"
  )
}
