package models

import play.api.data.validation.ValidationError
import play.api.libs.json.Reads._
import play.api.libs.json.{OWrites, Json, JsPath, Reads}
import play.api.libs.functional.syntax._

/**
	* Created by lli on 11/6/15.
	*/
case class Customer(hbid: String, slug: String, display_name: String, first_name: String, last_name: String, device_id: String, gender: String,
										avatar: String, phone: String, email: String, social_networks: SocialNetworks, registered: Boolean)

object Customer {
	implicit object CustomerWrites extends OWrites[Customer] {
		import play.api.libs.json._
		def writes(customer: Customer): JsObject = Json.obj(
			"hbid" -> customer.hbid,
			"slug" -> customer.slug,
			"display_name" -> customer.display_name,
			"first_name" -> customer.first_name,
			"last_name" -> customer.last_name,
			"device_id" -> customer.device_id,
			"gender" -> customer.gender,
			"avatar" -> customer.avatar,
			"phone" -> customer.phone,
			"email" -> customer.email,
			"social_networks" -> Json.obj(
        "facebook_username" -> customer.social_networks.facebook_username,
        "twitter_username" -> customer.social_networks.twitter_username,
        "instagram_username" -> customer.social_networks.instagram_username,
        "tumblr_username" -> customer.social_networks.tumblr_username,
        "google_username" -> customer.social_networks.google_username
      ),
			"registered" -> customer.registered
		)
	}
}