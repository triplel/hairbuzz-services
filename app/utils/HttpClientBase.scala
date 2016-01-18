package utils

import play.api.Logger

import scalaj.http.{HttpOptions, Http, HttpResponse}
import play.api.libs.json.{JsValue, JsObject, Json}

/**
 * Created by liliangli on 10/8/15.
 */
trait HttpClientBase {

  val connectionExceptionJson = Json.obj(
    "error" -> "connection refused when trying connecting to elasticsearch"
  )

  val exceptionOccurredJson = Json.obj(
    "error" -> "an unexpected exception occurred, please check system log"
  )


  def makeGetRequest(url:String): HttpResponse[String] = {
    Logger.underlyingLogger.info(s"GET - URL: $url")
    Http(url).option(HttpOptions.allowUnsafeSSL).asString
  }
  def makeGetRequest(url:String, params: Seq[(String,String)]): HttpResponse[String] = {
    Logger.underlyingLogger.info(s"GET - URL: $url Payload: ${params.mkString(",")}")
    Http(url).option(HttpOptions.allowUnsafeSSL).params(params).asString
  }
  def makePostRequest(url:String, params: Seq[(String,String)]): HttpResponse[String] = {
    Logger.underlyingLogger.info(s"POST - URL: $url Payload: ${params.mkString(",")}")
    Http(url).option(HttpOptions.allowUnsafeSSL).postForm(params).asString
  }

  /**
    * return as String
    * @param url
    * @param jsonPayload
    * @return
    */
  def makePostJsonRequest(url:String, jsonPayload: JsValue): HttpResponse[String] = {
    val jsonBody = Json.toJson(jsonPayload).toString()
    Logger.underlyingLogger.info(s"POST - URL: $url Payload: $jsonBody")
    Http(url).postData(jsonBody)
      .header("Content-Type", "application/json")
      .header("Charset", "UTF-8")
      .asString
  }
}
