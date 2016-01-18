package utils

import play.api.libs.json.{Writes, Reads, JsPath}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
  * Created by liliangli on 1/17/16.
  */
object SearchSuggestionHelper {

  //  localhost:9200/test/venue/_search?size=5&from=1
  //  {
  //    "fields" : ["address.street_address", "address.city", "address.state", "address.country"],
  //    "query" : {
  //      "term" : { "address.street_address" : "main" }
  //    }
  //  }

  //  case class VenueSuggestionSearchInput()

  /**
    * Suggested results of venues by address, city, state
    * @param hbid
    * @param name
    * @param street_address
    * @param city
    * @param state
    * @param country
    */
  case class VenueSuggestionResult(hbid: String, name: String, street_address: String, supplemental_address: String, city: String, state: String, country: String)
  implicit val venueSuggestSearchResultWrites: Writes[VenueSuggestionResult] = (
    (JsPath \ "hbid").write[String] and
      (JsPath \ "name").write[String] and
      (JsPath \ "street_address").write[String] and
      (JsPath \ "supplemental_address").write[String] and
      (JsPath \ "city").write[String] and
      (JsPath \ "state").write[String] and
      (JsPath \ "country").write[String]
    )(unlift(VenueSuggestionResult.unapply))


  case class VenueSuggestSearchInput(textInput: String, limit: Int, offset: Int)
  val venueSuggestionSearchInputReadsBuilder = (JsPath \ "text_input").read[String] and
    (JsPath \ "limit").read[Int](min(0)) and
    (JsPath \ "offset").read[Int](min(0))
  implicit val venueSuggestSearchInputReads: Reads[VenueSuggestSearchInput] = venueSuggestionSearchInputReadsBuilder.apply(VenueSuggestSearchInput.apply _)


  //  case class SearchInput(textInput: String, currentCoordinates: CurrentCoordinates, targetLocation: TargetLocation, limit: Int, offset: Int)
  //  //implicit reads for input json objects
  //  val searchInputReadsBuilder = (JsPath \ "text_input").read[String] and
  //    (JsPath \ "current_coordinates").read[CurrentCoordinates] and
  //    (JsPath \ "target_location").read[TargetLocation] and
  //    (JsPath \ "limit").read[Int](min(0)) and
  //    (JsPath \ "offset").read[Int](min(0))
  //  implicit val searchInputReads: Reads[SearchInput] = searchInputReadsBuilder.apply(SearchInput.apply _)
  //  //implicit writes for input json objects
  //  implicit val searchInputWrites: Writes[SearchInput] = (
  //    (JsPath \ "text_input").write[String] and
  //      (JsPath \ "current_coordinates").write[CurrentCoordinates] and
  //      (JsPath \ "target_location").write[TargetLocation] and
  //      (JsPath \ "limit").write[Int] and
  //      (JsPath \ "offset").write[Int]
  //    )(unlift(SearchInput.unapply))




  val venueSuggestionReadsBuilder = (JsPath \ "latitude").read[Double](min(-90.0) keepAnd max(90.0)) and
    (JsPath \ "longitude").read[Double](min(-180.0) keepAnd max(180.0))
//  implicit val venueSuggestionReads: Reads[VenueSuggestionResult] = venueSuggestionReadsBuilder.apply(VenueSuggestionResult.apply _)



}
