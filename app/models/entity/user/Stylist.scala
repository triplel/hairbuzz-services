package models.entity.user

import models.SocialNetworks
import play.api.libs.json.OWrites

/**
	* Created by lli on 11/20/15.
	*/
case class Stylist(hbid: String, slug: String, display_name: String, first_name: String, last_name: String, device_id: String, gender: String,
                   avatar: String, phone: String, email: String, social_networks: SocialNetworks, venue_hbid: String, registered: Boolean)

object Stylist {
	implicit object StylistWrites extends OWrites[Stylist] {
		import play.api.libs.json._
		def writes(stylist: Stylist): JsObject = Json.obj(
			"hbid" -> stylist.hbid,
			"slug" -> stylist.slug,
			"display_name" -> stylist.display_name,
			"first_name" -> stylist.first_name,
			"last_name" -> stylist.last_name,
			"device_id" -> stylist.device_id,
			"gender" -> stylist.gender,
			"avatar" -> stylist.avatar,
			"phone" -> stylist.phone,
			"email" -> stylist.email,
			"social_networks" -> Json.obj(
				"facebook_username" -> stylist.social_networks.facebook_username,
				"twitter_username" -> stylist.social_networks.twitter_username,
				"instagram_username" -> stylist.social_networks.instagram_username,
				"tumblr_username" -> stylist.social_networks.tumblr_username,
				"google_username" -> stylist.social_networks.google_username
			),
      "venue_hbid" -> stylist.venue_hbid,
			"registered" -> stylist.registered
		)
	}
}