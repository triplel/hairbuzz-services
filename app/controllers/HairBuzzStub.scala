package controllers

import play.api.mvc.{Action, Controller}
import play.api.libs.json._

/**
 * Created by liliangli on 9/25/15.
 */
object HairBuzzStub extends Controller{

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

  def stubGetUserByID(hbid: String) = Action {
    val jsonResponse = Json.obj(
      "bhid" -> "C0000000001",
      "device_id" -> "device_id_123123123",
      "gender" -> "male",
      "slug" -> "captain_america_chris",
      "avatar" -> "https://pbs.twimg.com/profile_images/605082381528096769/gt_sJRot_400x400.png",
      "first_name" -> "Chris",
      "last_name" -> "Evans",
      "phone" -> "2121239481",
      "email" -> "captain_america@newargehair.com",
      "social" -> Json.obj(
        "facebook_id" -> "chrisevans",
        "google_id" -> ""
      ),
      "registered" -> true
    )
    Ok(jsonResponse)
  }

  def stubSearchHairStylists = Action {

    val jsonResponse = Json.obj(
      "count" -> 3,
      "data" -> Json.arr(
        Json.obj(
          "hbid" -> "H0000000001",
          "first_name" -> "Megan",
          "last_name" -> "Fox",
          "slug" ->  "megan-fox",
          "avatar" -> "https://pbs.twimg.com/profile_images/3061372854/68fb99eb8e7d48180f583c61c1ef2613_bigger.jpeg",
          "rating" -> 9,
          "price_rank" -> 2,
          "venue" -> Json.obj(
            "venue_id" ->"V0000000009",
            "venue_name" ->  "Megan's Hair Salon",
            "address" -> Json.obj(
              "street_line1" -> "123 Main Street",
              "street_line2" -> "101",
              "city" -> "Flushing",
              "state" -> "NY",
              "zip" -> "11354",
              "country" -> "USA"
            )
          ),
          "coordinates" -> Json.obj(
            "latitude" -> 40.768452,
            "longitude" ->  -73.832764
          )
        ),
        Json.obj(
          "hbid" -> "H0000000002",
          "first_name" -> "Emma",
          "last_name" -> "Stone",
          "slug" ->  "emma-stone",
          "avatar" -> "https://pbs.twimg.com/profile_images/1752229650/icontwit_bigger.png",
          "rating" -> 8,
          "price_rank" -> 2,
          "venue" -> Json.obj(
            "venue_id" ->"V0000000002",
            "venue_name" ->  "New Age Hair Studio",
            "address" -> Json.obj(
              "street_line1" -> "219 W 44 Street",
              "street_line2" -> "",
              "city" -> "New York",
              "state" -> "NY",
              "zip" -> "10010",
              "country" -> "USA"
            )
          ),
          "coordinates" -> Json.obj(
            "latitude" -> 40.758896,
            "longitude" -> -73.985130
          )
        ),
        Json.obj(
          "hbid" -> "H0000000003",
          "first_name" -> "Tony",
          "last_name" -> "Stark",
          "slug" ->  "tony_iron_man",
          "avatar" -> "https://pbs.twimg.com/profile_images/454731838502600704/RvnCcrdC_bigger.jpeg",
          "rating" -> 7,
          "price_rank" -> 2,
          "venue" -> Json.obj(
            "venue_id" ->"V0000000003",
            "venue_name" ->  "The Hulk Buster",
            "address" -> Json.obj(
              "street_line1" -> "201 Prince Street",
              "street_line2" -> "502",
              "city" -> "Brooklyn",
              "state" -> "NY",
              "zip" -> "11201",
              "country" -> "USA"
            )
          ),
          "coordinates" -> Json.obj(
            "latitude" -> 40.650002,
            "longitude" ->  -73.949997
          )
        )
      )
    )
    Ok(jsonResponse)
  }
}
