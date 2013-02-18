import sbt._
import Keys._

object ScalikejdbcConfigBuild extends Build {

  lazy val root = Project (
    id = "scalikejdbc-config",
    base = file ("."),
    settings = Defaults.defaultSettings ++ Seq (
      name := "scalikejdbc-config",
      organization := "com.github.tototoshi",
      scalaVersion := "2.10.0",
      version := "0.1.0-SNAPSHOT",
      libraryDependencies ++= Seq(
        "com.github.seratch" %% "scalikejdbc" % "1.4.4" % "provided",
        "com.typesafe" % "config" % "1.0.0",
        "org.slf4j" % "slf4j-simple" % "1.7.2" % "provided",
        "com.h2database" % "h2" % "[1.3,)" % "test",
        "org.scalatest" %% "scalatest" % "1.9.1" % "test"
      ),
      initialCommands := "import com.github.tototoshi.scalikejdbc.config._; import scalikejdbc._"
    )
  )
}

