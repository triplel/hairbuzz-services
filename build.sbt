import com.typesafe.config._

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

name := conf.getString("hairbuzz.services.display.name")

version := conf.getString("hairbuzz.services.version")

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache
)

libraryDependencies += "org.apache.httpcomponents" % "httpclient" % "4.5"

libraryDependencies +=  "org.scalaj" %% "scalaj-http" % "1.1.5"

play.Project.playScalaSettings