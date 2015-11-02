package utils

import java.net.ConnectException

import play.api.Logger
import play.api.libs.json.{JsArray, JsValue, Json}
/**
 * Created by liliangli on 10/8/15.
 */
object SearchClient extends HttpClientBase with FormatSearch{
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
   * a lite search based on relevance from _all field
   * @param searchInput SearchInput object
   * @return
   */
  def liteFullTextSearch(searchInput: SearchDataHelper.SearchInput, esIndex: String, esType: String): JsValue ={
    val getURL = s"$searchMainURL/$esIndex/$esType/_search?q=${normalizeSearchString(searchInput.textInput)}&size=${searchInput.limit}&from=${searchInput.offset}"
    liteSearch(getURL,searchInput)
  }

	/**
	 * a lite search for stylists
	 * @param searchInput SearchInput object
	 * @return
	 */
	def liteSearchStylist(searchInput: SearchDataHelper.SearchInput): JsValue = {
    liteFullTextSearch(searchInput,devIndex,devStylistType)
	}

	/**
	 * a lite search on venue
	 * @param searchInput SearchInput object
	 * @return
	 */
	def liteSearchVenue(searchInput: SearchDataHelper.SearchInput): JsValue = {
    liteFullTextSearch(searchInput,devIndex,devVenueType)
	}

	def liteSearch(url: String, searchInput: SearchDataHelper.SearchInput): JsValue = {
		try{
			val results = makeGetRequest(url).body
			val responseJson = Json.parse(results)
      val total = responseJson.\("hits")\("total")
			val hitsSeq = (responseJson.\("hits")\("hits")).as[Seq[JsValue]]
			val resultSeq = hitsSeq.map(x => x.\("_source").get)
			val searchResult = Json.obj(
				"total"-> total.get,
				"count" -> searchInput.limit,
				"offset" -> searchInput.offset,
				"results" -> JsArray(resultSeq)
			)
      logger.info(s"$total results found, showing ${searchInput.offset} to ${searchInput.limit + searchInput.offset} ($url)")
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
	 * a specific search based on search policy logic (current coordinates/target location, first, last, studio name, hair style)
	 * @param searchInput SearchInput object
	 * @return
	 */
	def fullBodySearch(searchInput: SearchDataHelper.SearchInput):JsValue = {
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
