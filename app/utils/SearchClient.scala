package utils

import java.net.{ConnectException, InetAddress}

import play.api.Logger
import play.api.libs.json.{JsArray, JsValue, JsObject, Json}
/**
 * Created by liliangli on 10/8/15.
 */
object SearchClient extends HttpClientBase {
	private val logger = Logger.underlyingLogger
	private val searchMainURL =  play.Play.application().configuration().getString("elasticsearch.main.url")

	//todo - use configuration to control dev or prod
	private val devIndex =  play.Play.application().configuration().getString("elasticsearch.dev.index")
	private val devVenueType =  play.Play.application().configuration().getString("elasticsearch.dev.venue.type")
	private val devStylistType =  play.Play.application().configuration().getString("elasticsearch.dev.stylist.type")

	private val connectionExceptionJson = Json.obj(
		"error" -> "connection refused when trying connecting to elasticsearch"
	)

	private val exceptionOccurredJson = Json.obj(
		"error" -> "an unexpected exception occurred, please check system log"
	)

	/**
	 * a lite search based on scores of all fields inverted indexes for stylists
	 * @param searchInput SearchInput object
	 * @return
	 */
	def liteSearchStylist(searchInput: SearchDataHelper.SearchInput): JsValue = {
		val tokenizedString = searchInput.textInput.replace(" ","+")
		val getURL = s"$searchMainURL/$devIndex/$devStylistType/_search?q=${searchInput.textInput}&size=${searchInput.limit}&from=${searchInput.offset}"
		liteSearch(getURL,searchInput)
	}

	/**
	 * a lite search based on scores of all fields inverted indexes for venues
	 * @param searchInput SearchInput object
	 * @return
	 */
	def liteSearchVenue(searchInput: SearchDataHelper.SearchInput): JsValue = {
		val getURL = s"$searchMainURL/$devIndex/$devVenueType/_search?q=${searchInput.textInput}&size=${searchInput.limit}&from=${searchInput.offset}"
		liteSearch(getURL,searchInput)
	}

	def liteSearch(url: String, searchInput: SearchDataHelper.SearchInput): JsValue = {
		try{
			val results = makeGetRequest(url).body
			val responseJson = Json.parse(results)
			val hitsSeq = (responseJson.\("hits")\("hits")).as[Seq[JsValue]]
			val resultSeq = hitsSeq.map(x => x.\("_source"))
			val searchResult = Json.obj(
				"total"-> responseJson.\("hits")\("total"),
				"count" -> searchInput.limit,
				"offset" -> searchInput.offset,
				"results" -> JsArray(resultSeq)
			)
			searchResult
		}catch {
			case ce: ConnectException => {
				logger.info(s"ConnectionException - ${ce.getMessage}")
				logger.debug(ce.getStackTraceString)
				connectionExceptionJson
			}
			case e: Exception => {
				logger.info(s"Exception - ${e.getMessage}")
				logger.debug(e.getStackTraceString)
				exceptionOccurredJson
			}
		}
	}

	/**
	 * a specific search based on search policy logics (current coordinates/target location, first, last, studio name, hair style)
	 * @param searchInput SearchInput object
	 * @return
	 */
	def searchVenue(searchInput: SearchDataHelper.SearchInput):JsValue = {
		val postURL = s"$searchMainURL/$devIndex/$devVenueType/_search"
		try{
			//todo - implement search object with search policy logic
			val inputJson = Json.obj(
				"query" -> Json.obj(
					"match" -> Json.obj(
					)
				)
			)
			val results = makePostJsonRequest(postURL,inputJson).body
			Json.toJson(results)
		}catch {
			case ce: ConnectException => {
				logger.info(s"ConnectionException - ${ce.getMessage}")
				logger.debug(ce.getStackTraceString)
				connectionExceptionJson
			}
			case e: Exception => {
				logger.info(s"Exception - ${e.getMessage}")
				logger.debug(e.getStackTraceString)
				exceptionOccurredJson
			}
		}
	}

}
