package controllers

import play.api.libs.json.{JsError, Json}
import play.api.mvc._
import utils.{SearchClient, SearchDataHelper}

/**
	* Created by lli on 11/2/15.
	*/
class SearchController extends Controller {
	//todo - investigate why using elastic4s would cause JVM to abort error
	//	val elasticClient = ElasticClient.local

	/**
		* controller function for search
		* @return
		*/
	def searchVenue() = Action(BodyParsers.parse.json) { request =>
		val searchInput = request.body.validate[SearchDataHelper.SearchInput]
		searchInput.fold(
			error => {
				BadRequest(Json.obj("status" -> "Invalid Input", "message" -> JsError.toFlatJson(error)))
			},
			input => {
				val result = SearchClient.liteSearchVenue(input)
				Ok(result)
			}
		)
	}

	def searchStylist() = Action(BodyParsers.parse.json) { request =>
		val searchInput = request.body.validate[SearchDataHelper.SearchInput]
		searchInput.fold(
			error => {
				BadRequest(Json.obj("status" -> "Invalid Input", "message" -> JsError.toFlatJson(error)))
			},
			input => {
				val result = SearchClient.liteSearchStylist(input)
				Ok(result)
			}
		)
	}
}
