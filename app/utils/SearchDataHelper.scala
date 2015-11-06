package utils

import play.api.libs.json.{Writes, Reads, JsPath}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._

/**
 * Created by lli on 10/8/15.
 */
object SearchDataHelper {
	//input case classes for JSON
	/**
	 * Current user's Cartesian coordinates
	 * @param latitude latitude (-90.0 to 90.0)
	 * @param longitude longitude (-180.0 to 180.0)
	 */
	case class CurrentCoordinates(latitude: Double, longitude: Double)
	/**
	 * Location that has neighbourhood, city and zip code to narrow the location based search
	 * @param neighbourhood e.g.: Flushing, Tribeca, Chinatown, etc.
	 * @param city e.g.: Manhattan, Queens, Boston
	 * @param zip e.g: 11201, 08310-1821
	 */
	case class TargetLocation(neighbourhood: String, city: String, zip: String)
	/**
	 * Main search object
	 * @param textInput anything use can type in the search box (stylist name, studio name, hairstyle, etc.)
	 * @param currentCoordinates user's current Cartesian coordinates
	 * @param targetLocation the target location user is interested in searching
	 * @param limit number of results to be retrieved
	 * @param offset pagination on result list
	 */
	case class SearchInput(textInput: String, currentCoordinates: CurrentCoordinates, targetLocation: TargetLocation, limit: Int, offset: Int)
	//implicit reads for input json objects
	val currentCoordinatesReadsBuilder = (JsPath \ "latitude").read[Double](min(-90.0) keepAnd max(90.0)) and
		(JsPath \ "longitude").read[Double](min(-180.0) keepAnd max(180.0))
	implicit val currentCoordinatesReads: Reads[CurrentCoordinates] = currentCoordinatesReadsBuilder.apply(CurrentCoordinates.apply _)
	val targetLocationReadsBuilder = (JsPath \ "neighbourhood").read[String] and
		(JsPath \ "city").read[String] and
		(JsPath \ "zip").read[String]
	implicit val targetLocationReads: Reads[TargetLocation] = targetLocationReadsBuilder.apply(TargetLocation.apply _)
	val searchInputReadsBuilder = (JsPath \ "text_input").read[String] and
		(JsPath \ "current_coordinates").read[CurrentCoordinates] and
		(JsPath \ "target_location").read[TargetLocation] and
		(JsPath \ "limit").read[Int](min(0)) and
		(JsPath \ "offset").read[Int](min(0))
	implicit val searchInputReads: Reads[SearchInput] = searchInputReadsBuilder.apply(SearchInput.apply _)
	//implicit writes for input json objects
	implicit val currentCoordinatesWrites: Writes[CurrentCoordinates] = (
		(JsPath \ "latitude").write[Double] and (JsPath \ "longitude").write[Double]
		)(unlift(CurrentCoordinates.unapply))
	implicit val targetLocationWrites: Writes[TargetLocation] = (
		(JsPath \ "neighbourhood").write[String] and (JsPath \ "city").write[String] and (JsPath \ "zip").write[String]
		)(unlift(TargetLocation.unapply))
	implicit val searchInputWrites: Writes[SearchInput] = (
		(JsPath \ "text_input").write[String] and
			(JsPath \ "current_coordinates").write[CurrentCoordinates] and
			(JsPath \ "target_location").write[TargetLocation] and
			(JsPath \ "limit").write[Int] and
			(JsPath \ "offset").write[Int]
		)(unlift(SearchInput.unapply))
}
