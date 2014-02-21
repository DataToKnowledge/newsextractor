package it.dtk.extractor

import it.dtk.DataRecordExtractor
import scala.xml.{NodeSeq, Elem}

/**
 * @author Andrea Scarpino <andrea@datatoknowledge.it>
 */
class LeccePrimaDataRecordExtractor extends DataRecordExtractor {

  override def newsDetailUrl(e: Elem): String = (e \ "header" \ "h1" \ "a" \ "@href").text

  override def summary(e: Elem): String = (e \ "p").text

  override def title(e: Elem): String = (e \ "header" \ "h1").text

  override def dataRecordXPath(e: Elem): NodeSeq = e \\ "article" filter { _ \ "@class" exists (_.text == "post p-small clearfix") }
}
