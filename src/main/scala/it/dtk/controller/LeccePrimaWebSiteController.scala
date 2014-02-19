package it.dtk.controller

import it.dtk.WebSiteController
import akka.actor.Props

/**
 * @author Andrea Scarpino <me@andreascarpino.it>
 */
class LeccePrimaWebSiteController extends WebSiteController {

  override val maxIncrement: Int = 843

  override val baseUrl: String = "http://www.lecceprima.it/cronaca/"

  override def dataRecordExtractorProps: Props = ???

  override def logicalListUrlGenerator(start: Int, stop: Int): Seq = ???
}
