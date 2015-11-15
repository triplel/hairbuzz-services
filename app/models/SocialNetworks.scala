package models

import play.api.libs.json.OWrites

/**
	* Created by lli on 11/6/15.
	*/
case class SocialNetworks(facebook_username: String, twitter_username: String, instagram_username: String,
													tumblr_username: String, google_username: String)

object SocialNetworks{
	implicit object SocialNetworksWrites extends OWrites[SocialNetworks] {
		import play.api.libs.json._
		def writes(sns: SocialNetworks): JsObject = Json.obj(
			"facebook_username" -> sns.facebook_username,
			"twitter_username" -> sns.twitter_username,
			"instagram_username" -> sns.instagram_username,
			"tumblr_username" -> sns.tumblr_username,
			"google_username" -> sns.google_username
		)
	}
}