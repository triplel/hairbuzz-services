package models

import play.api.data.validation.ValidationError
import play.api.libs.json.{Reads, JsPath}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
	* Created by lli on 11/6/15.
	*/
object JsonFormats {
	import play.api.libs.json.Json
	import play.api.data._
	import play.api.data.Forms._

		implicit val socialNetworksFormat = Json.format[SocialNetworks]
		implicit val customerFormat = Json.format[Customer]
}
