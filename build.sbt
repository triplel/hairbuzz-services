import com.typesafe.config._

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

name := conf.getString("hairbuzz.services.display.name")

version := conf.getString("hairbuzz.services.version")

name := """hairbuzz-services"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  specs2 % Test
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "1.1.5"

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "1.1.5"
