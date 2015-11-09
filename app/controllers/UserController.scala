package controllers

import javax.inject.Inject

import models.Customer
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument
import reactivemongo.core.errors.DatabaseException
import utils.{PostUserDataHelper, SearchClient, SearchDataHelper}
import models._
import models.JsonFormats._

import scala.concurrent.Future

import play.api.Logger
import play.api.mvc.{BodyParsers, Action, Controller}
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json._

import scala.util.{ Failure, Success }
import scala.concurrent.ExecutionContext.Implicits.global

// Reactive Mongo imports
import reactivemongo.api.Cursor

import play.modules.reactivemongo.{ // ReactiveMongo Play2 plugin
MongoController,
ReactiveMongoApi,
ReactiveMongoComponents
}

// BSON-JSON conversions/collection
import play.modules.reactivemongo.json._
import play.modules.reactivemongo.json.collection._


/**
  * Created by lli on 11/2/15.
  */
class UserController @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller
with MongoController with ReactiveMongoComponents {

  private val logger = Logger.underlyingLogger

  def stylist_collection: JSONCollection = db.collection[JSONCollection]("stylists")
  def customer_collection: JSONCollection = db.collection[JSONCollection]("customers")

  def findCustomer(customer: Customer): Future[Option[Customer]] = Future.successful(Option(customer))

  def getCustomerByHbid(hbid: String) = Action {
    Ok(s"Should retrieve customer by hbid -> $hbid")
  }

  def getCustomerBySlug(slug: String) = Action.async {
    val query = BSONDocument("slug" -> slug)
    val customerAsFuture: Future[Option[Customer]] = customer_collection.
      find(query).
      one[Customer]

    val response = for {
      maybeCustomer <- customerAsFuture
      result <- maybeCustomer.map(findCustomer).getOrElse(Future.successful(Option.empty))
    } yield result

    response.map{
      customer => Ok(Customer.CustomerWrites.writes(customer.get))
    }
  }

  def getStylistByHbid(hbid: String) = Action {
    Ok(s"Should retrieve stylist by hbid -> $hbid")
  }

  def getStylistBySlug(slug: String) = Action.async {
    val cursor: Cursor[JsObject] = stylist_collection.
      find(Json.obj("slug" -> slug)).
      cursor[JsObject]
    // gather all the JsObjects in a list
    val futurePersonsList: Future[List[JsObject]] = cursor.collect[List]()
    // transform the list into a JsArray
    val futurePersonsJsonArray: Future[JsArray] =
      futurePersonsList.map { stylist => Json.arr(stylist) }
    futurePersonsJsonArray.map { stylist =>
      Ok(stylist)
    }
  }

  def insertCustomer = Action.async(parse.json) { request =>
    request.body.validate[PostUserDataHelper.Customer].map { customer =>
      customer_collection.insert(customer).map { lastError =>
        logger.debug(s"Successfully inserted with LastError: $lastError")
        logger.info(s"Customer inserted! (name:${customer.first_name} ${customer.last_name} email:${customer.email} slug:${customer.display_name})")
        Created(Json.obj("status" -> "success", "message" -> s"Customer inserted! (name:${customer.first_name} ${customer.last_name} email:${customer.email} slug:${customer.display_name})"))
      }.recover{
        case dbe: DatabaseException => {
          logger.info(dbe.getMessage())
          logger.debug(dbe.getStackTraceString)
          //todo
          if (dbe.code.get == 11001 || dbe.code.get == 11000){
            Conflict(dbe.getMessage())
          }
          else{
            InternalServerError(dbe.getMessage())
          }
        }
        case e: Exception => {
          logger.info(e.getMessage())
          logger.debug(e.getStackTraceString)
          InternalServerError(e.getMessage())
        }
        case _ => {
          InternalServerError("An unknown exception occcurred")
        }
      }
    }.getOrElse(Future.successful(BadRequest("invalid json for customer")))
  }

  def insertCustomer2() = Action(BodyParsers.parse.json) { request =>
    val searchInput = request.body.validate[Customer]
    searchInput.fold(
      error => {
        BadRequest(Json.obj("status" -> "Invalid Input", "message" -> JsError.toFlatJson(error)))
      },
      validCustomer => {
        val future = customer_collection.insert(validCustomer)
        ////	      val responseJson = Json.
        future.onComplete {
          case Failure(e) => {
            logger.info(s"Exception - ${e.getLocalizedMessage}")
            logger.debug(s"Exception - ${e.getStackTrace}")
            InternalServerError(e.getMessage)
          }
          case Success(writeResult) => {
            logger.info(s"Customer inserted! (name:${validCustomer.first_name} ${validCustomer.last_name} email:${validCustomer.email} slug:${validCustomer.slug})")
            Created(Json.obj())
          }
        }
        Ok(validCustomer.toString)
      }
    )
  }
}