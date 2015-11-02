package utils

import org.apache.commons.lang3.StringUtils
import play.api.data.validation.ValidationError
import play.api.libs.json.{Json, Writes, Reads, JsPath}
import play.api.libs.json.Reads._
import play.api.libs.functional.syntax._


/**
  * Created by liliangli on 11/5/15.
  */
object UserDataHelper {

  //todo - create better validation for names, phone numbers, etc.
  //  def onlyAlphabets[T](implicit r: Reads[T]): Reads[T] = Reads.filterNot(ValidationError("validate.error.unexpected.value"))((StringUtils.isAlpha(_))(Boolean))
  //  def onlyNumerical[T](implicit r: Reads[T]): Reads[T] = Reads.filterNot(ValidationError("validate.error.unexpected.value"))((StringUtils.isNumeric(_))[Boolean])
  def notEqual[T](v: T)(implicit r: Reads[T]): Reads[T] = Reads.filterNot(ValidationError("validate.error.unexpected.value", v))(_ == v)

  case class SocialNetwroks(facebook_username: String, twitter_username: String, instagram_username: String,
                            tumblr_username: String, google_username: String)
  case class Customer(slug: String, first_name: String, last_name: String, device_id: String, gender: String,
                     avatar: String, phone: String, email: String, social_networks: SocialNetwroks, registered: Boolean)

  val socialNetwroksReadsBuilder = (JsPath \ "facebook_username").read[String](minLength[String](2)) and
    (JsPath \ "twitter_username").read[String] and
    (JsPath \ "instagram_username").read[String] and
    (JsPath \ "tumblr_username").read[String] and
    (JsPath \ "google_username").read[String]
  implicit val socialNetwroksReads: Reads[SocialNetwroks] = socialNetwroksReadsBuilder.apply(SocialNetwroks.apply _)
	implicit val socialNetworkJsonWrites = Json.writes[SocialNetwroks]

  val customerReadsBuilder = (JsPath \ "slug").read[String](minLength[String](2)) and
    (JsPath \ "first_name").read[String](notEqual("")) and
    (JsPath \ "last_name").read[String](notEqual("")) and
    (JsPath \ "device_id").read[String](notEqual("")) and
    (JsPath \ "gender").read[String](notEqual("")) and
    (JsPath \ "avatar").read[String] and
    (JsPath \ "phone").read[String] and
    (JsPath \ "email").read[String](Reads.email) and
    (JsPath \ "social_networks").read[SocialNetwroks] and
    (JsPath \ "registered").read[Boolean]
  implicit val customerReads: Reads[Customer] = customerReadsBuilder.apply(Customer.apply _)
	implicit val customerJsonWrites = Json.writes[Customer]
}
