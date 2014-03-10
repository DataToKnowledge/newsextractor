package it.dtk.extractor

import it.dtk.DataRecordExtractor
import it.dtk.DataRecordExtractor.{ DataRecord, DataRecords }
import java.util.Date
import org.jsoup.Jsoup
import scala.collection.JavaConversions._
import org.jsoup.nodes.Element
import it.dtk.DataRecordExtractor._
import org.joda.time.DateTime
import scala.util.Success
import scala.util.Failure
import it.dtk.HttpGetter

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class BariTodayDataRecordExtractor extends DataRecordExtractor {
  
  override val cssRecordsSelector: String = "article[class= post p-small clearfix]"

  def title(node: Element) =
    node.select("header > figure > a > img[alt]").attr("alt")

  def summary(node: Element) =
    node.select("p").text

  def newsUrl(node: Element) =
    node.select("header > h1 > a").attr("href")

}
