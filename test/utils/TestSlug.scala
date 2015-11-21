package utils

import org.scalatest.{Matchers, FlatSpec}

/**
  * Created by liliangli on 11/18/15.
  */
class TestSlug extends FlatSpec with Matchers{

  "A customer with name 'John O'connell" should "have slug john-oconnel-1" in {
    val slug = Slug.slugify("John O'connell 1")
    slug should be ("john-oconnell-1")
  }

  "Another customer with name 'John O'connell" should "have slug john-oconnel-2" in {
    val slug = Slug.slugify("John O.connell 2")
    slug should be ("john-oconnell-2")
  }

}
