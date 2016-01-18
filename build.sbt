import com.typesafe.config._

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

name := conf.getString("hairbuzz.services.display.name")

version := conf.getString("hairbuzz.services.version")

name := """hairbuzz-services"""

version := "0.0.7-SNAPSHOT"

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

libraryDependencies ++= Seq(
  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "org.scalaj" %% "scalaj-http" % "1.1.5",
  "org.apache.httpcomponents" % "httpclient" % "4.5",
  "org.scalaj" %% "scalaj-http" % "1.1.5",
	"org.reactivemongo" %% "play2-reactivemongo" % "0.11.7.play24",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)