package utils

/**
 * Created by liliangli on 10/15/15.
 */
trait FormatSearch {

  def normalizeSearchString(query: String): String = {
    query replaceAll(" ",",") replaceAll("[^a-zA-Z0-9,]", "")
  }

}
