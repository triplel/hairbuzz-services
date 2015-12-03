package utils

import play.api.Logger

import scala.util.{Success, Failure}
import models.Customer
import play.api.libs.json.{JsArray, Json, JsObject}
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import reactivemongo.bson.{BSONRegex, BSONDocument}
import utils.PostUserDataHelper.CustomerInput

import scala.concurrent.Future
import play.modules.reactivemongo.json._
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by liliangli on 11/9/15.
  */
object MongoOpsHelper {

  private val logger = Logger.underlyingLogger

  def getLastSlugIncr(collection: JSONCollection, slugRegex: String): Int = {
    val query = BSONDocument("slug" -> BSONRegex("^" + slugRegex, "i"))
    //    val sort = BSONDocument("slug" -> BSONDocument("$regex" -> slugRegex))

    val queryJson = Json.obj("slug" -> Json.obj("$regex" -> slugRegex))
    val docsWithSlugFuture =
      collection.
        find(queryJson).
        cursor[JsObject]().
        collect[List]()
    val resultList = for {
      list <- docsWithSlugFuture.value.flatMap(_.toOption)
    } yield list

    if(resultList.isDefined){
      if(resultList.get.isEmpty)
        0
      else{
        //take list of slugs, filter out the chars, find max in the postfix value
        val slugs = resultList.get
        val sortedSlugs:List[Int] = slugs.map(x => (x \ "slug_incr").get.as[Int])
        sortedSlugs.max
      }
    }else{
      0
    }
  }

  //this is based on the assumption that first and last are not empty
  def incrementSlug(slug: String): String = {
    val slugParts = slug.split("-")
    val incrNum = slugParts(2).toInt + 1
    SlugHelper.slugify(s"${slugParts(0)} ${slugParts(1)} $incrNum")
  }

  def createCustomerSlug(collection: JSONCollection, customer: CustomerInput): String = {
    //todo - check if first & last name are empty
    val slugRegex = SlugHelper.slugify(s"${customer.first_name} ${customer.last_name}")
    val lastSlugIncr:Int = getLastSlugIncr(collection, slugRegex) + 1
    SlugHelper.slugify(s"${customer.first_name} ${customer.last_name} $lastSlugIncr")

  }
}
