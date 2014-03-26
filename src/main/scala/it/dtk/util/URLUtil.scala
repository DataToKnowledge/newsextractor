package it.dtk.util

import java.net.{URLEncoder, MalformedURLException}
import scala.util.{Try, Success, Failure}

/**
 * URL utilities class.
 *
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
object URLUtil {

  val URL_REGEX = "^http(s?)://" +                // Scheme
    "[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})" +   // Domain
    "(\\:[0-9]{1,5})?" +                          // Port
    "[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*"         // Resource and Query

  /**
   * Normalize a given URL to http(s)://domain.tld/(whatever/) format.
   * @param url URL to normalize
   * @return Normalized URL or MalformedURLException
   */
  def normalize(url: String): Try[String] = {
    var parsedUrl = url.trim.toLowerCase

    // Remove indexes
    if (parsedUrl.contains("#"))
      parsedUrl = parsedUrl.substring(0, parsedUrl.indexOf("#"))

    // Remove useless query args concatenator
    if (parsedUrl.endsWith("&"))
      parsedUrl = parsedUrl.substring(0, parsedUrl.length - 1)

    // Remove empty query
    if (parsedUrl.endsWith("?"))
      parsedUrl = parsedUrl.substring(0, parsedUrl.length - 1)

    // Add http if missing
    if (!parsedUrl.startsWith("http://") && !parsedUrl.startsWith("https://"))
      parsedUrl = "http://" + parsedUrl

    // Add trailing slash if missing
    if (!parsedUrl.endsWith("/") && getRequestedResource(parsedUrl, fullQuery = true) == None)
      parsedUrl = parsedUrl + "/"

    // Check if composed URL is still valid (try also to encode URL if validation fails)
    if (parsedUrl.matches(URL_REGEX)) Success(parsedUrl)
    else if (encodeURL(parsedUrl).matches(URL_REGEX)) Success(encodeURL(parsedUrl))
    else Failure(new MalformedURLException(s"URL is malformed: $url"))
  }

  /**
   * Normalize a given base URL with given relative path
   * to http(s)://baseUrl/relativePath/ format.
   * @param baseUrl Base URL
   * @param relativePath Relative path to append to base URL
   * @return Normalized baseUrl + relativePath or MalformedURLException
   */
  def normalize(baseUrl: String, relativePath: String): Try[String] = {
    val lowerBaseUrl = baseUrl.trim.toLowerCase
    val lowerRelativePath = relativePath.trim.toLowerCase

    if (!isAbsolute(lowerBaseUrl))
      Failure(new MalformedURLException(s"Base URL is not absolute: $lowerBaseUrl"))
    if (!isRelative(lowerRelativePath))
      Failure(new MalformedURLException(s"Path is not relative: $lowerRelativePath"))

    if (!lowerBaseUrl.endsWith("/") && !lowerRelativePath.startsWith("/"))
      normalize(lowerBaseUrl + "/" + lowerRelativePath)
    else if (lowerBaseUrl.endsWith("/") && lowerRelativePath.startsWith("/"))
      normalize(lowerBaseUrl + lowerRelativePath.substring(1))
    else
      normalize(lowerBaseUrl + lowerRelativePath)
  }

  /**
   * Returns requested resource from URL.
   * @param url URL to analyze
   * @param fullQuery If true, full query will be returned (e.g.: index.php?param1=val1[...])
   * @return Resource name if resource is specified; None otherwise
   */
  def getRequestedResource(url: String, fullQuery: Boolean = false): Option[String] = {
    val RESOURCE_FRAGMENT_REGEX = "((.+)\\.(.{2,5}))?(\\?.+)?"
    val loweredUrl = url.trim.toLowerCase
    val parsedUrl = loweredUrl.split("/")

    if (loweredUrl.startsWith("http") && parsedUrl.size >= 4) {
      if (parsedUrl.last.matches(RESOURCE_FRAGMENT_REGEX)) {
        val indexQuery = parsedUrl.last.indexOf("?")
        val res: Option[String] = if (indexQuery != -1) Option(parsedUrl.last.substring(0, indexQuery)) else None
        if (!fullQuery && res.isDefined) {
          if (!res.get.isEmpty) Option(res.get) else None
        } else Option(parsedUrl.last)
      } else None
    } else if (isRelative(loweredUrl)) {
      if (parsedUrl.last.matches(RESOURCE_FRAGMENT_REGEX)) {
        val indexQuery = parsedUrl.last.indexOf("?")
        val res: Option[String] = if (indexQuery != -1) Option(parsedUrl.last.substring(0, indexQuery)) else None
        if (!fullQuery && res.isDefined) {
          if (!res.get.isEmpty) Option(res.get) else None
        } else Option(parsedUrl.last)
      } else None
    } else if (loweredUrl.startsWith("/")) {
      if (parsedUrl.last.matches(RESOURCE_FRAGMENT_REGEX)) {
        val indexQuery = parsedUrl.last.indexOf("?")
        val res: Option[String] = if (indexQuery != -1) Option(parsedUrl.last.substring(0, indexQuery)) else None
        if (!fullQuery && res.isDefined) {
          if (!res.get.isEmpty) Option(res.get) else None
        } else Option(parsedUrl.last)
      } else None
    } else None
  }

  /**
   * Returns requested query arguments from URL.
   * @param url URL to analyze
   * @return Map of (argument,value) if there are arguments; None otherwise
   */
  def getQueryArgs(url: String): Option[Map[String, String]] = {
    val loweredUrl = url.trim.toLowerCase
    val res = getRequestedResource(loweredUrl, fullQuery = true)
    val indexQuery = if (res.isDefined && res.get.indexOf("?") != -1) Option(res.get.indexOf("?")) else None

    if (indexQuery.isDefined) {
      val query = res.get.substring(indexQuery.get)
      if (query.isEmpty) None

      val pattern = "[\\?\\&]([^\\?\\&]+)\\=([^\\?\\&]+)".r
      var valuesMap: Map[String, String] = Map.empty
      pattern.findAllIn(query).matchData.foreach(m => valuesMap += (m.group(1) -> m.group(2)))
      if (!valuesMap.isEmpty) Option(valuesMap)
      else None
    } else None
  }

  /**
   * Get domain of a given URL.
   * @param url URL to parse
   * @return Domain of URL (format: domain.tld) or MalformedURLException
   */
  def getDomainName(url: String): Try[String] = {
    var parsedUrl = url.trim.toLowerCase

    // Prune leading http or https
    if (!parsedUrl.startsWith("http") && !parsedUrl.startsWith("https"))
      parsedUrl = parsedUrl.split("/").apply(0)
    else
      parsedUrl = parsedUrl.split("/").apply(2)

    val urlTokens = parsedUrl.split("\\.")

    // Compose domain name or throw exception
    if (urlTokens.length > 1)
      Success(urlTokens.apply(urlTokens.length - 2) + "." + urlTokens.last.replaceFirst("\\:[0-9]+", ""))
    else
      Failure(new MalformedURLException(s"URL is malformed: $url"))
  }

  /**
   * Encode given URL (replaces invalid characters with corresponding HTML entities).
   * @param url URL to encode
   * @return Encoded URL
   */
  def encodeURL(url: String): String = {
    val loweredUrl = url.trim.toLowerCase
    val fullRes = getRequestedResource(loweredUrl, fullQuery = true)

    if (!fullRes.isDefined) {
      loweredUrl
    } else {
      val encodedRes = URLEncoder.encode(getRequestedResource(loweredUrl).getOrElse(""), "UTF-8")

      val query = getQueryArgs(loweredUrl)
      if (query.isDefined) {
        val encodedQuery = query.get.mapValues(URLEncoder.encode(_, "UTF-8"))
        var encQueryStr = ""
        encodedQuery.foreach(m => encQueryStr += "&" + m._1 + "=" + m._2)
        encQueryStr = encQueryStr.replaceFirst("&", "?")
        loweredUrl.replace(fullRes.get, encodedRes) + encQueryStr
      } else loweredUrl.replace(fullRes.get, encodedRes)
    }
  }

  /**
   * Check if given URL is normalized.
   * @param url URL to check
   * @return true if is normalized; false otherwise
   */
  def isNormalized(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    (lowerCaseUrl.endsWith("/")
      && (lowerCaseUrl.startsWith("https")
      || lowerCaseUrl.startsWith("http")))
  }

  /**
   * Check if given URL is absolute (http(s)://domain.tld/(whatever).
   * @param url URL to check
   * @return true if is absolute; false otherwise
   */
  def isAbsolute(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    (lowerCaseUrl.startsWith("https")
      || lowerCaseUrl.startsWith("http"))
  }

  /**
   * Check if given URL is relative (whatever/ or ./whatever/ or ../whatever/).
   * @param url URL to check
   * @return true if is relative; false otherwise
   */
  def isRelative(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    (!lowerCaseUrl.startsWith("https")
      && !lowerCaseUrl.startsWith("http"))
  }

}
