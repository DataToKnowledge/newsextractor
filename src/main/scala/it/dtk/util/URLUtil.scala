package it.dtk.util

import java.net.MalformedURLException

/**
 * URL utilities class.
 *
 * @author Michele Damiano Torelli <me@mdtorelli.it>
 */
object URLUtil {
  
  val URL_REGEX = "^(http(?:s)?\\:\\/\\/[a-zA-Z0-9\\-]+(?:\\.[a-zA-Z0-9\\-]+)*\\.[a-zA-Z]{2,6}(?:\\/?|(?:\\/[\\w\\-]+)*)(?:\\/?|\\/\\w+\\.[a-zA-Z]{2,4}(?:\\?[\\w]+\\=[\\w\\-]+)?)?(?:\\&[\\w]+\\=[\\w\\-]+)*)$"

  /**
   * Normalize a given URL to http(s)://domain.tld/(whatever/) format.
   * @param url URL to normalize
   * @return Normalized URL
   */
  def normalize(url: String): Option[String] = {
    val urlToLower = url.trim.toLowerCase
    val urlNormalize = urlToLower match {
      case u if u.contains("#") => u.substring(0, u.indexOf("#"))
      case u if !u.startsWith("http://") && !u.startsWith("https://") => "http://" + u
      case u if !u.endsWith("/") => u + "/"
    } 

    // Check if composed URL is still valid
    if (urlNormalize.matches(URL_REGEX)) Some(urlNormalize) else None
  }

  /**
   * Get domain of a given URL.
   * @param url URL to parse
   * @return Domain of URL (format: domain.tld)
   */
  def getDomainName(url: String): String = {
    var parsedUrl = url.trim.toLowerCase

    // Prune leading http or https
    if (!parsedUrl.startsWith("http") && !parsedUrl.startsWith("https")) {
      parsedUrl = parsedUrl.split("/").apply(0)
    } else {
      parsedUrl = parsedUrl.split("/").apply(2)
    }

    val urlTokens = parsedUrl.split("\\.")

    if (urlTokens.length > 1) {
      // Compose domain name
      urlTokens.apply(urlTokens.length - 2) + "." + urlTokens.last
    } else {
      // Passed URL is malformed
      throw new MalformedURLException(s"URL is malformed: $url")
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

    (!lowerCaseUrl.startsWith("/")
    && !lowerCaseUrl.startsWith("//")
    && !lowerCaseUrl.startsWith("http")
    && !lowerCaseUrl.startsWith("https"))
  }

}
