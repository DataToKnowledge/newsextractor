package it.dtk.extractor

import it.dtk.DataRecordExtractor
import scala.xml.NodeSeq
import it.dtk.DataRecordExtractor.{ DataRecord, DataRecords }
import java.util.Date
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.jsoup.nodes.Element
import scala.collection.JavaConversions._

class SenzaColonneDataRecordExtractor extends DataRecordExtractor {

  override val cssRecordsSelector: String = "div.itemContainer"

  def title(node: Element) = node.getElementsByClass("catItemTitle").text()

  def summary(node: Element) = null

  def newsUrl(node: Element) = node.getElementsByClass("catItemTitle").select("a").attr("href")

  def data(node: Element) = node.getElementsByClass("catItemDateCreated").text
}

