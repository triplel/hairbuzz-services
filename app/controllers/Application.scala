package controllers

import play.api._
import play.api.libs.json.Json
import play.api.mvc._

class Application extends Controller {

	val appVersion = play.Play.application().configuration().getString("hairbuzz.services.version")
	val appName = play.Play.application().configuration().getString("hairbuzz.services.display.name")

	def index = Action {
    val jsonResponse = Json.obj(
      "application" -> appName,
      "version" -> appVersion,
      "message" -> s"${appName} (${appVersion}) is running."
    )
    Ok(jsonResponse)
  }
}
