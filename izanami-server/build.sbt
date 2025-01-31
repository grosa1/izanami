import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

name := """izanami"""

Universal / packageName := "izanami"
Universal / name := "izanami"

scalaVersion := Dependencies.scalaVersion

lazy val ITest = config("it") extend Test

val gitCommitId = SettingKey[String]("gitCommitId")
gitCommitId := git.gitHeadCommit.value.getOrElse("Not Set")

lazy val `izanami-server` = (project in file("."))
  .configs(ITest)
  .settings(Defaults.itSettings: _*)
  .enablePlugins(PlayScala, SwaggerPlugin, DockerPlugin, BuildInfoPlugin)
  .settings(publish / skip := true)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](
        version,
        gitCommitId
      ),
    buildInfoPackage := "buildinfo"
  )

val metricsVersion    = "4.0.2"
val kotlinVersion     = "1.3.70"
val doobieVersion     = "0.13.4"
val silencerVersion   = "1.4.4"
val prometheusVersion = "0.8.1"

resolvers ++= Seq(
  Resolver.jcenterRepo,
  Resolver.sonatypeRepo("releases"),
  Resolver.bintrayIvyRepo("sohoffice", "sbt-plugins"),
  ("streamz at bintray" at "http://dl.bintray.com/streamz/maven").withAllowInsecureProtocol(true)
)

libraryDependencies ++= Seq(
  ws,
  jdbc,
  javaWs,
  ehcache,
  "de.svenkubiak"            % "jBCrypt"                         % "0.4.1", //  ISC/BSD
  "com.auth0"                % "java-jwt"                        % "3.3.0", // MIT license
  "com.nimbusds"             % "nimbus-jose-jwt"                 % "8.0",
  "org.gnieh"                %% "diffson-play-json"              % "4.0.2", //
  "com.softwaremill.macwire" %% "macros"                         % "2.3.3" % "provided", // Apache 2.0
  "org.scala-lang.modules"   %% "scala-collection-compat"        % "2.1.4",
  "com.typesafe.akka"        %% "akka-http"                      % Dependencies.akkaHttpVersion, // Apache 2.0
  "com.typesafe.akka"        %% "akka-actor"                     % Dependencies.akkaVersion, // Apache 2.0
  "com.typesafe.akka"        %% "akka-slf4j"                     % Dependencies.akkaVersion, // Apache 2.0
  "com.typesafe.akka"        %% "akka-stream"                    % Dependencies.akkaVersion, // Apache 2.0
  "com.typesafe.akka"        %% "akka-actor-typed"               % Dependencies.akkaVersion, // Apache 2.0
  "com.typesafe.akka"        %% "akka-cluster"                   % Dependencies.akkaVersion, // Apache 2.0
  "com.typesafe.akka"        %% "akka-cluster-tools"             % Dependencies.akkaVersion, // Apache 2.0
  "dev.zio"                  %% "zio"                            % "1.0.1",
  "dev.zio"                  %% "zio-interop-cats"               % "2.1.4.0",
  "org.reactivemongo"        %% "reactivemongo-akkastream"       % "0.20.3",
  "org.reactivemongo"        %% "reactivemongo-play-json-compat" % "0.20.3-play28",
  "org.reactivemongo"        %% "play2-reactivemongo"            % "0.20.3-play28",
  "com.lightbend.akka"       %% "akka-stream-alpakka-dynamodb"   % Dependencies.alpakkaVersion, // Apache 2.0
  "io.lettuce"               % "lettuce-core"                    % "5.2.2.RELEASE", // Apache 2.0
  "org.iq80.leveldb"         % "leveldb"                         % "0.12", // Apache 2.0
  "org.typelevel"            %% "cats-core"                      % "2.2.0", // MIT license
  "org.typelevel"            %% "cats-effect"                    % "2.2.0", // MIT license
  "org.tpolecat"             %% "doobie-core"                    % doobieVersion,
  "org.tpolecat"             %% "doobie-hikari"                  % doobieVersion,
  "org.tpolecat"             %% "doobie-postgres"                % doobieVersion,
  "org.postgresql"           % "postgresql"                      % "42.2.25", // remove this lib when doobie support postgresql >= 42.2.25 (CVE-2022-21724)
//  "com.github.krasserm"      %% "streamz-converter"                   % "0.11-RC1",
  "com.chuusai"           %% "shapeless"                           % "2.3.3", // Apache 2.0
  "com.github.pureconfig" %% "pureconfig"                          % "0.12.3", // Apache 2.0
  "com.typesafe.akka"     %% "akka-stream-kafka"                   % "2.0.0", // Apache 2.0
  "com.adelegue"          %% "elastic-scala-http"                  % "1.0.0", // Apache 2.0
  "io.dropwizard.metrics" % "metrics-core"                         % metricsVersion, // Apache 2.0
  "io.dropwizard.metrics" % "metrics-jvm"                          % metricsVersion, // Apache 2.0
  "io.dropwizard.metrics" % "metrics-jmx"                          % metricsVersion, // Apache 2.0
  "io.dropwizard.metrics" % "metrics-json"                         % metricsVersion, // Apache 2.0
  "io.prometheus"         % "simpleclient_common"                  % prometheusVersion, // Apache 2.0
  "io.prometheus"         % "simpleclient_dropwizard"              % prometheusVersion, // Apache 2.0
  "org.scala-lang"        % "scala-compiler"                       % scalaVersion.value,
  "org.scala-lang"        % "scala-library"                        % scalaVersion.value,
  "net.java.dev.jna"      % "jna"                                  % "5.5.0",
  "org.jetbrains.kotlin"  % "kotlin-stdlib-jdk8"                   % kotlinVersion,
  "org.jetbrains.kotlin"  % "kotlin-script-runtime"                % kotlinVersion,
  "org.jetbrains.kotlin"  % "kotlin-script-util"                   % kotlinVersion,
  "org.jetbrains.kotlin"  % "kotlin-compiler-embeddable"           % kotlinVersion,
  "org.jetbrains.kotlin"  % "kotlin-scripting-compiler-embeddable" % kotlinVersion,
  "org.webjars"           % "swagger-ui"                           % "3.25.0",
  "net.logstash.logback"  % "logstash-logback-encoder"             % "6.3",
  "com.typesafe.akka"     %% "akka-testkit"                        % Dependencies.akkaVersion % "it,test", // Apache 2.0
  "de.heikoseeberger"     %% "akka-http-play-json"                 % "1.31.0" % "it,test" excludeAll ExclusionRule(
    "com.typesafe.play",
    "play-json"
  ), // Apache 2.0
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3"  % "it,test", // Apache 2.0
  "com.github.kstyrc"      % "embedded-redis"      % "0.6"    % "it,test", // Apache 2.0
  "org.slf4j"              % "slf4j-api"           % "1.7.36" % "it,test", // MIT license
//  "org.apache.logging.log4j" % "log4j-api"           % "2.16.0"  % "it,test", // MIT license
  "org.apache.logging.log4j" % "log4j-api"  % "2.17.0", // MIT license // Enforce 2.17 for the bundle
  "org.apache.logging.log4j" % "log4j-core" % "2.17.0" % "it,test", // MIT license
  "net.logstash.logback" % "logstash-logback-encoder" % "5.3" //Apache 2.0
)

dependencyOverrides ++= Seq(
  "org.slf4j" % "slf4j-api" % "1.7.36"
) // avoid slf4j-api >= 2. Remove this override when doobie >= 1.0.0

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full)

ITest / scalaSource := baseDirectory.value / "it"
ITest / resourceDirectory := (baseDirectory apply { baseDir: File => baseDir / "it/resources" }).value

scalacOptions ++= Seq(
  "-feature",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:existentials",
//  "-Ywarn-unused:imports",
  "-Xfatal-warnings",
  "-Yrangepos"
)

//addCompilerPlugin(scalafixSemanticdb)

coverageExcludedPackages := "<empty>;Reverse.*;router\\.*"

Compile / doc / sources := Seq.empty

Compile / packageDoc / publishArtifact := false

Test / parallelExecution := false

swaggerDomainNameSpaces := Seq(
  "domains.abtesting.events.ExperimentVariantEventKey",
  "domains.abtesting.events.ExperimentVariantDisplayed",
  "domains.abtesting.events.ExperimentVariantWon",
  "domains.abtesting.Traffic",
  "domains.abtesting.Variant",
  "domains.abtesting.Campaign",
  "domains.abtesting.CurrentCampaign",
  "domains.abtesting.ClosedCampaign",
  "domains.apikey",
  "domains.config",
  "domains.events",
  "domains.feature",
  "domains.script",
  "domains.user",
  "domains.webhook",
  "controllers.dto.abtesting",
  "controllers.dto.apikeys",
  "controllers.dto.config",
  "controllers.dto.script",
  "controllers.dto.user",
  "controllers.dto.meta",
  "controllers.dto.importresult"
)
swaggerV3 := true

/// ASSEMBLY CONFIG
assembly / mainClass := Some("play.core.server.ProdServerStart")
assembly / test := {}
assembly / assemblyJarName := "izanami.jar"
assembly / fullClasspath += Attributed.blank(PlayKeys.playPackageAssets.value)
assembly / assemblyMergeStrategy := {
  case PathList("javax", xs @ _*)                                             => MergeStrategy.first
  case PathList("net", "jpountz", xs @ _*)                                    => MergeStrategy.first
  case PathList("org", "jetbrains", xs @ _*)                                  => MergeStrategy.first
  case PathList("META-INF", "native", xs @ _*)                                => MergeStrategy.first
  case PathList("org", "apache", "commons", "logging", xs @ _*)               => MergeStrategy.discard
  case PathList("zio", xs @ _*) if xs.lastOption.contains("BuildInfo$.class") => MergeStrategy.first
  case PathList(xs @ _*) if xs.lastOption.contains("module-info.class")       => MergeStrategy.first
  case PathList(ps @ _*) if ps.last == "io.netty.versions.properties"         => MergeStrategy.first
  case PathList(ps @ _*) if ps.contains("reference-overrides.conf")           => MergeStrategy.concat
  case PathList(ps @ _*) if ps.exists(_.endsWith(".kotlin_module"))           => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".conf"                          => MergeStrategy.concat
//  case PathList(ps @ _*) if ps.contains("buildinfo")                          => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith "reflection-config.json" => MergeStrategy.first
  case o =>
    val oldStrategy = (assembly / assemblyMergeStrategy).value
    oldStrategy(o)
}

lazy val packageAll = taskKey[Unit]("PackageAll")
packageAll := {
  (Compile / dist).value
  (Compile / assembly).value
}

/// DOCKER CONFIG
dockerExposedPorts := Seq(
  2551,
  8080
)
Docker / packageName := "izanami"

Docker / dockerPackageMappings += (baseDirectory.value / "docker" / "start.sh") -> "/opt/docker/bin/start.sh"

Docker / maintainer := "MAIF Team <maif@maif.fr>"

dockerBaseImage := "eclipse-temurin:11"

dockerCommands := dockerCommands.value.filterNot {
  case ExecCmd("CMD", args @ _*) => true
  case cmd                       => false
}

dockerCommands ++= Seq(
  Cmd("ENV", "APP_NAME izanami"),
  Cmd("ENV", "APP_VERSION 1.0.6-SNAPSHOT"),
  Cmd("ENV", "LEVEL_DB_PARENT_PATH /leveldb"),
  Cmd("ENV", "REDIS_PORT 6379"),
  Cmd("ENV", "REDIS_HOST redis"),
  Cmd("ENV", "KAFKA_HOST kafka"),
  Cmd("ENV", "KAFKA_PORT 9092"),
  Cmd("ENV", "HTTP_PORT 8080"),
  Cmd("ENV", "APPLICATION_SECRET 2nJS=TpH/qBfB=NI6:H/jt3@5B3IBhzD4OjWi=tCH50Bjy2=JCXO^]XeZUW47Gv4"),
  Cmd("ENV", "IZANAMI_FILTER_DEFAULT_FAIL_ON_DEFAULT_VALUE true")
)

dockerExposedVolumes ++= Seq(
  "/leveldb",
  "/data"
)

dockerUsername := Some("maif")

dockerEntrypoint := Seq("/opt/docker/bin/start.sh")

dockerUpdateLatest := true

lazy val generateDoc = taskKey[Unit]("Copy api doc")

generateDoc := {
  val p = project
  (Compile / swagger).value
  val swaggerFile = target.value / "swagger" / "swagger.json"
  val targetDoc   = p.base.getParentFile / "docs" / "swagger" / "swagger.json"
  IO.delete(targetDoc)
  IO.copyDirectory(swaggerFile, targetDoc)
}
