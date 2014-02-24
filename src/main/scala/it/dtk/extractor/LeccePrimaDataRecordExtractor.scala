package it.dtk.extractor

import it.dtk.DataRecordExtractor
import scala.xml.{XML, NodeSeq, Node}
import it.dtk.DataRecordExtractor.{DataRecord, DataRecords}
import java.util.Date

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class LeccePrimaDataRecordExtractor(url: String, html: String, date: Date) extends DataRecordExtractor {

  //get the data records
  val records = dataRecordXPath(XML.loadString(html))

  context.parent ! new DataRecords(url, date, records.map(r => {
    DataRecord(title(r), summary(r), newsUrl(r))
  }))

  override def newsUrl(e: Node): String = (e \ "header" \ "h1" \ "a" \ "@href").text

  override def summary(e: Node): String = (e \ "p").text

  override def title(e: Node): String = (e \ "header" \ "h1").text

  override def dataRecordXPath(e: Node): NodeSeq = e \\ "article" filter {
    _ \ "@class" exists (_.text == "post p-small clearfix")
  }
}
