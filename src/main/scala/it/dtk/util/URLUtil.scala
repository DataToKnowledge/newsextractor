package it.dtk.util

import java.net.MalformedURLException
import scala.util.{ Try, Success, Failure }

/**
 * URL utilities class.
 *
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
object URLUtil {

  val URL_REGEX = "^http(s{0,1})://[a-zA-Z0-9_/\\-\\.]+\\.([A-Za-z/]{2,5})[a-zA-Z0-9_/\\&\\?\\=\\-\\.\\~\\%]*"

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

    // Add http if missing
    if (!parsedUrl.startsWith("http://") && !parsedUrl.startsWith("https://"))
      parsedUrl = "http://" + parsedUrl

    // Add trailing slash if missing
    if (!parsedUrl.endsWith("/") && getRequestedResource(parsedUrl) == None)
      parsedUrl = parsedUrl + "/"

    // Check if composed URL is still valid
    if (parsedUrl.matches(URL_REGEX)) Success(parsedUrl)
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

    if (!lowerBaseUrl.endsWith("/"))
      normalize(lowerBaseUrl + "/" + lowerRelativePath)
    else
      normalize(lowerBaseUrl + lowerRelativePath)
  }

  /**
   * Returns requested resource from URL
   * @param url URL to analyze
   * @return Resource name if resource is specified; None otherwise
   */
  def getRequestedResource(url: String): Option[String] = {
    val loweredUrl = url.trim.toLowerCase
    val parsedUrl = loweredUrl.split("/")

    if (loweredUrl.startsWith("http") && parsedUrl.size >= 4) {
      if (parsedUrl.last.matches("(.+)\\.(.{2,4})")) Some(parsedUrl.last) else None
    } else if (isRelative(loweredUrl)) {
      if (parsedUrl.last.matches("(.+)\\.(.{2,4})")) Some(parsedUrl.last) else None
    } else if (loweredUrl.startsWith("/")) {
      if (parsedUrl.last.matches("(.+)\\.(.{2,4})")) Some(parsedUrl.last) else None
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
    if (urlTokens.length > 1) Success(urlTokens.apply(urlTokens.length - 2) + "." + urlTokens.last)
    else Failure(new MalformedURLException(s"URL is malformed: $url"))
  }

  /**
   * Check if given URL is normalized.
   * @param url URL to check
   * @return true if is normalized; false otherwise
   */
  def isNormalized(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    (lowerCaseUrl.endsWith("/")
      && (lowerCaseUrl.startsWith("http")
        || lowerCaseUrl.startsWith("https")))
  }

  /**
   * Check if given URL is absolute (http(s)://domain.tld/(whatever) or /path-to-whatever).
   * @param url URL to check
   * @return true if is absolute; false otherwise
   */
  def isAbsolute(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    if (lowerCaseUrl.startsWith("/"))
      return true

    (lowerCaseUrl.startsWith("http")
      || lowerCaseUrl.startsWith("https"))
  }

  /**
   * Check if given URL is relative (whatever/ or ./whatever/ or ../whatever/).
   * @param url URL to check
   * @return true if is relative; false otherwise
   */
  def isRelative(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase
    !lowerCaseUrl.startsWith("http") && !lowerCaseUrl.startsWith("https")
    //TODO check if it should check for / and //
  }

}
