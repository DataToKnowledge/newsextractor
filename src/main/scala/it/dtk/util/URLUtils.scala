package it.dtk.util

import java.net.MalformedURLException
import java.lang.ArrayIndexOutOfBoundsException

/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 17/02/14
 * Time: 10:59
 */
object URLUtils {
  def normalize(url: String): String = {
    val URL_REGEX = "^(http(?:s)?\\:\\/\\/[a-zA-Z0-9\\-]+(?:\\.[a-zA-Z0-9\\-]+)*\\.[a-zA-Z]{2,6}(?:\\/?|(?:\\/[\\w\\-]+)*)(?:\\/?|\\/\\w+\\.[a-zA-Z]{2,4}(?:\\?[\\w]+\\=[\\w\\-]+)?)?(?:\\&[\\w]+\\=[\\w\\-]+)*)$"
    var parsedUrl = url.trim.toLowerCase

    if (parsedUrl.contains("#")) {
      parsedUrl = parsedUrl.substring(0, parsedUrl.indexOf("#"))
    }

    if (!parsedUrl.startsWith("http://") && !parsedUrl.startsWith("https://")) {
      parsedUrl = "http://" + parsedUrl
    }

    if (!parsedUrl.endsWith("/")) {
      parsedUrl += "/"
    }

    if (parsedUrl matches URL_REGEX) {
      parsedUrl
    } else {
      throw new MalformedURLException(s"URL is malformed: $url")
    }
  }

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

  def isNormalized(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    (lowerCaseUrl.endsWith("/")
    && (lowerCaseUrl.startsWith("http")
    || lowerCaseUrl.startsWith("https")))
  }

  def isAbsolute(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    if (lowerCaseUrl.startsWith("/")) {
      return true
    }

    (lowerCaseUrl.startsWith("http")
    || lowerCaseUrl.startsWith("https"))
  }

  def isRelative(url: String): Boolean = {
    val lowerCaseUrl = url.trim.toLowerCase

    (!lowerCaseUrl.startsWith("/")
    && !lowerCaseUrl.startsWith("//")
    && !lowerCaseUrl.startsWith("http")
    && !lowerCaseUrl.startsWith("https"))
  }
}
