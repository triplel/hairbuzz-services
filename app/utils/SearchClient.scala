package utils

import java.net.ConnectException
import play.api.Logger
import play.api.libs.json.{JsArray, JsValue, Json}
import utils.SearchSuggestionHelper.VenueSuggestSearchInput

/**
  * Created by liliangli on 10/8/15.
  */
object SearchClient extends HttpClientBase with FormatSearch{
  private val logger = Logger.underlyingLogger
  private val searchMainURL =  play.Play.application().configuration().getString("elasticsearch.main.url")
  private val DEFAULT_DISTANCE_UNIT = "km"

  //todo - use configuration to control dev or prod
  private val devIndex =  play.Play.application().configuration().getString("elasticsearch.dev.index")
  private val devVenueType =  play.Play.application().configuration().getString("elasticsearch.dev.venue.type")
  private val devStylistType =  play.Play.application().configuration().getString("elasticsearch.dev.stylist.type")

  /**
    * a lite search based on relevance from _all field
    * @param searchInput SearchInput object
    * @return
    */
  def liteFullTextSearch(searchInput: SearchDataHelper.LiteSearchInput, esIndex: String, esType: String): JsValue ={
    val getURL = s"$searchMainURL/$esIndex/$esType/_search?q=${normalizeSearchString(searchInput.textInput)}&size=${searchInput.limit}&from=${searchInput.offset}"
    liteSearch(getURL,searchInput)
  }

  def geoFullTextSearch(searchInput: SearchDataHelper.LiteSearchInput, esIndex: String, esType: String): JsValue = {
    val esIndex = devIndex
    val esType = devStylistType
    val url = s"$searchMainURL/$esIndex/$esType/_search"
    val postJson = Json.obj(
      "query" -> Json.obj(
        "filtered" -> Json.obj(
          "query" -> Json.obj(
            "match" -> Json.obj(
              "_all" -> searchInput.textInput
            )
          ),
          "filter" -> Json.obj(
            "geo_distance" -> Json.obj(
              "distance" -> s"${searchInput.radius}$DEFAULT_DISTANCE_UNIT",
              "coordinates" -> Json.obj(
                "lat" -> searchInput.coordinates.latitude,
                "lon" -> searchInput.coordinates.longitude
              )
            )
          )
        )
      ),
      "size" -> searchInput.limit,
      "from" -> searchInput.offset
      )
    try {
      val results = makePostJsonRequest(url, postJson).body
      val responseJson = Json.parse(results)
      val total = responseJson.\("hits") \ ("total")
      val hitsSeq = (responseJson.\("hits") \ ("hits")).as[Seq[JsValue]]
      val resultSeq = hitsSeq.map(x => x.\("_source").get)
      val searchResult = Json.obj(
        "total" -> total.get,
        "count" -> searchInput.limit,
        "offset" -> searchInput.offset,
        "results" -> JsArray(resultSeq)
      )
      logger.info(s"${total.get} results found, showing ${searchInput.offset} to ${searchInput.limit + searchInput.offset} ($url)")
      searchResult
    } catch {
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
    * a lite search for stylists
    * @param searchInput SearchInput object
    * @return
    */
  def liteSearchStylist(searchInput: SearchDataHelper.LiteSearchInput): JsValue = {
    liteFullTextSearch(searchInput,devIndex,devStylistType)
  }

  def geoSearchStylist(searchInput: SearchDataHelper.LiteSearchInput): JsValue = {
    geoFullTextSearch(searchInput,devIndex,devStylistType)
  }

  /**
    * a lite search on venue
    * @param searchInput SearchInput object
    * @return
    */
  def liteSearchVenue(searchInput: SearchDataHelper.LiteSearchInput): JsValue = {
    liteFullTextSearch(searchInput,devIndex,devVenueType)
  }

  def suggestSearchVenue(suggestSearchInput: VenueSuggestSearchInput): JsValue = {
    val esIndex = devIndex
    val esType = devVenueType
    val url = s"$searchMainURL/$esIndex/$esType/_search"
    val postJson = Json.obj(
      "from" -> suggestSearchInput.offset,
      "size" -> suggestSearchInput.limit,
      "fields" -> Json.arr("hbid", "name", "address.street_address", "address.supplemental_address", "address.city",
        "address.state", "address.country", "address.zip", "coordinates.latitude", "coordinates.longitude"),
      "query" -> Json.obj(
        "multi_match" -> Json.obj(
          "query" -> suggestSearchInput.textInput,
          "fields" -> Json.arr("name", "address.street_address", "address.supplemental_address", "address.city", "address.state")
        )
      ))
    try {
      val results = makePostJsonRequest(url, postJson).body
      val responseJson = Json.parse(results)
      val total = responseJson.\("hits") \ ("total")
      val hitsSeq = (responseJson.\("hits") \ ("hits")).as[Seq[JsValue]]
      val resultSeq = hitsSeq.map(x => x.\("fields").get)
      .map(x => Json.obj(
        "hbid" -> x.\("hbid").get(0).get,
        "name" -> x.\("name").get(0).get,
        "street_address" -> x.\("address.street_address").get(0).get,
        "supplemental_address" -> x.\("address.supplemental_address").get(0).get,
        "city" -> x.\("address.city").get(0).get,
        "state" -> x.\("address.state").get(0).get,
        "zip" -> x.\("address.zip").get(0).get,
        "country" -> x.\("address.country").get(0).get,
        "coordinates" -> Json.obj(
          "latitude" -> x.\("coordinates.latitude").get(0).get,
          "longitude" -> x.\("coordinates.longitude").get(0).get
        )
      ))
      val suggestionArray = JsArray(resultSeq)
      val searchResult = Json.obj(
        "total" -> total.get,
        "count" -> suggestSearchInput.limit,
        "offset" -> suggestSearchInput.offset,
        "results" -> suggestionArray
      )
      logger.info(s"${total.get} results found, showing ${suggestSearchInput.offset} to ${suggestSearchInput.limit + suggestSearchInput.offset} ($url)")
      searchResult
    } catch {
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

  def liteSearch(url: String, searchInput: SearchDataHelper.LiteSearchInput): JsValue = {
    try {
      val results = makeGetRequest(url).body
      val responseJson = Json.parse(results)
      val total = responseJson.\("hits") \ ("total")
      val hitsSeq = (responseJson.\("hits") \ ("hits")).as[Seq[JsValue]]
      val resultSeq = hitsSeq.map(x => x.\("_source").get)
      val searchResult = Json.obj(
        "total" -> total.get,
        "count" -> searchInput.limit,
        "offset" -> searchInput.offset,
        "results" -> JsArray(resultSeq)
      )
      logger.info(s"$total results found, showing ${searchInput.offset} to ${searchInput.limit + searchInput.offset} ($url)")
      searchResult
    } catch {
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
  def fullBodySearch(searchInput: SearchDataHelper.LiteSearchInput): JsValue = {
    val postURL = s"$searchMainURL/$devIndex/$devVenueType/_search"
    try {
      //todo - implement search object with search policy logic
      val inputJson = Json.obj(
        "query" -> Json.obj(
          "match" -> Json.obj(
          )
        )
      )
      val results = makePostJsonRequest(postURL, inputJson).body
      Json.toJson(results)
    } catch {
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