ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.4"

val http4sVersion = "0.23.30"
val doobieVersion = "1.0.0-RC6"

lazy val root = (project in file("."))
  .settings(
    name := "qa_volsmart",
    libraryDependencies ++= Seq(

      "org.http4s" %% "http4s-ember-client" % http4sVersion,
      "org.http4s" %% "http4s-ember-server" % http4sVersion,
      "org.http4s" %% "http4s-dsl"          % http4sVersion,
      "org.typelevel" %% "cats-effect" % "3.5.7",

      "org.http4s" %% "http4s-circe" % http4sVersion,
      "io.circe" %% "circe-generic" % "0.14.10",

      "org.typelevel" %% "cats-core" % "2.12.0",
      "org.typelevel" %% "cats-effect" % "3.5.7",

      "ch.qos.logback" % "logback-classic" % "1.5.15",

      // Start with this one
      "org.tpolecat" %% "doobie-core"      % doobieVersion,
      "org.tpolecat" %% "doobie-hikari"    % doobieVersion,
      "org.tpolecat" %% "doobie-postgres"  % doobieVersion,
      "org.tpolecat" %% "doobie-specs2"    % doobieVersion % "test",
      "org.tpolecat" %% "doobie-scalatest" % doobieVersion % "test",

      "org.scalatest"     %% "scalatest"                % "3.2.19"        % Test
    )
  )