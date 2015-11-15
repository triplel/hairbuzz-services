package utils

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by liliangli on 11/18/15.
  */
class TestMongoOpsHelper extends FlatSpec with Matchers{

  "If slug bruce-wayne-99 already exist, then we " should "have a new slug bruce-wayne-100" in {
    val slug = MongoOpsHelper.incrementSlug("bruce-wayne-99")
    slug should be ("bruce-wayne-100")
  }

}
