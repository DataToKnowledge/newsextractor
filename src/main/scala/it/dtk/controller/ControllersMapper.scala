package it.dtk.controller

import akka.actor.Props


/**
 * Created by fabiofumarola on 08/02/15.
 */
object ControllersMapper {

  def props(controllerName: String): Option[Props] =
    controllerName match {

      case name if name == "BariTodayWebSiteController" =>
        Option(BariTodayWebSiteController.props(name))

      case name if name == "BrindisiLiberaWebSiteController" =>
        Option(BrindisiLiberaWebSiteController.props(name))

      case name if name == "BrindisiReportWebSiteController" =>
        Option(BrindisiReportWebSiteController.props(name))

      case name if name == "CorriereSalentinoWebSiteController" =>
        Option(CorriereSalentinoWebSiteController.props(name))

      case name if name == "CorriereWebSiteController" =>
        Option(CorriereWebSiteController.props(name))

      case name if name == "GoBariWebSiteController" =>
        Option(GoBariWebSiteController.props(name))

      case name if name ==  "LeccePrimaWebSiteController" =>
        Option(LeccePrimaWebSiteController.props(name))

      case name if name == "Puglia24NewsWebSiteController" =>
        Option(Puglia24NewsWebSiteController.props(name))

      case name if name == "QuotidianoDiPugliaWebSiteController" =>
        Option(QuotidianoDiPugliaWebSiteController.props(name))

      case name if name == "RepubblicaWebSiteController" =>
        Option(RepubblicaWebSiteController.props(name))

      case name if name == "SenzaColonneWebSiteController" =>
        Option(SenzaColonneWebSiteController.props(name))

      case _ =>
        None
    }

}
