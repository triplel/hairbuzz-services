package models

/**
	* Created by lli on 11/20/15.
	*/
case class Stylist(hbid: String, slug: String, display_name: String, first_name: String, last_name: String, device_id: String, gender: String,
                   avatar: String, phone: String, email: String, social_networks: SocialNetworks, registered: Boolean)

object Stylist {
	
}
