package it.dtk.util

import scala.util.Try
import scala.annotation.tailrec
import edu.uci.ics.crawler4j.util.URLCanonicalizer

object UrlCAnonicalizer {

  def getCanonicalURL(url: String): String = ???

  def getCanonicalURL(baseUrl: String, relativeUrl: String): Try[String] =
    Try {
      URLCanonicalizer.getCanonicalURL(baseUrl, relativeUrl)
    }

}

object UrlResolver {

  /**
   * Resolves a given relative URL against a base URL. See
   * <a href="http://www.faqs.org/rfcs/rfc1808.html">RFC1808</a>
   * Section 4 for more details.
   *
   * @param baseUrl     The base URL in which to resolve the specification.
   * @param relativeUrl The relative URL to resolve against the base URL.
   * @return the resolved specification.
   */
  def resolve(baseUrl: String, relativeUrl: String): Try[String] = Try {
    edu.uci.ics.crawler4j.util.UrlResolver.resolveUrl(baseUrl, relativeUrl)
  }

  /**
   * Resolves a given relative URL against a base URL using the algorithm
   * depicted in <a href="http://www.faqs.org/rfcs/rfc1808.html">RFC1808</a>:
   *
   * Section 4: Resolving Relative URLs
   *
   *   This section describes an example algorithm for resolving URLs within
   *   a context in which the URLs may be relative, such that the result is
   *   always a URL in absolute form. Although this algorithm cannot
   *   guarantee that the resulting URL will equal that intended by the
   *   original author, it does guarantee that any valid URL (relative or
   *   absolute) can be consistently transformed to an absolute form given a
   *   valid base URL.
   *
   * @param baseUrl     The base URL in which to resolve the specification.
   * @param relativeUrl The relative URL to resolve against the base URL.
   * @return the resolved specification.
   */
  private def resolve(baseUrl: Url, relativeUrl: String): Option[Url] = {
    var url = parseUrl(relativeUrl)

    // Step 1: The base URL is established according to the rules of
    //         Section 3.  If the base URL is the empty string (unknown),
    //         the embedded URL is interpreted as an absolute URL and
    //         we are done.

    // Step 2: Both the base and embedded URLs are parsed into their
    //         component parts as described in Section 2.4.
    //      a) If the embedded URL is entirely empty, it inherits the
    //         entire base URL (i.e., is set equal to the base URL)
    //         and we are done.
    if (relativeUrl.size == 0)
      return Option(url)

    //      b) If the embedded URL starts with a scheme name, it is
    //         interpreted as an absolute URL and we are done.
    if (url.scheme == None)
      return Option(url)

    //      c) Otherwise, the embedded URL inherits the scheme of
    //         the base URL.
    url = url.copy(scheme = baseUrl.scheme)
    // Step 3: If the embedded URL's <net_loc> is non-empty, we skip to
    //         Step 7.  Otherwise, the embedded URL inherits the <net_loc>
    //         (if any) of the base URL.
    if (url.location != None)
      return Option(url)

    url = url.copy(location = baseUrl.location)
    // Step 4: If the embedded URL path is preceded by a slash "/", the
    //         path is not relative and we skip to Step 7.

    val urlPath = url.path.filter(_.length() > 0).filter(_.charAt(0) == '/').map(removeLeadingSlashPoints(_))
    if (urlPath.isDefined)
      return Some(url.copy(path = urlPath))

    // Step 5: If the embedded URL path is empty (and not preceded by a
    //         slash), then the embedded URL inherits the base URL path,
    //         and

    None
  }

  @tailrec
  private def removeLeadingSlashPoints(path: String): String =
    if (!path.startsWith("/.."))
      path
    else
      removeLeadingSlashPoints(path.substring(3))

  def parseUrl(url: String): Url = {

    var startIndex = 0
    var endIndex = url.length() - 1

    // Section 2.4.1: Parsing the Fragment Identifier
    //
    //   If the parse string contains a crosshatch "#" character, then the
    //   substring after the first (left-most) crosshatch "#" and up to the
    //   end of the parse string is the <fragment> identifier. If the
    //   crosshatch is the last character, or no crosshatch is present, then
    //   the fragment identifier is empty. The matched substring, including
    //   the crosshatch character, is removed from the parse string before
    //   continuing.
    //
    //   Note that the fragment identifier is not considered part of the URL.
    //   However, since it is often attached to the URL, parsers must be able
    //   to recognize and set aside fragment identifiers as part of the
    //   process.
    val urlFragment: Option[String] = indexOf(url, '#', startIndex, endIndex).
      map(index => url.substring(index + 1, endIndex))

    // Section 2.4.2: Parsing the Scheme
    //
    //   If the parse string contains a colon ":" after the first character
    //   and before any characters not allowed as part of a scheme name (i.e.,
    //   any not an alphanumeric, plus "+", period ".", or hyphen "-"), the
    //   <scheme> of the URL is the substring of characters up to but not
    //   including the first colon. These characters and the colon are then
    //   removed from the parse string before continuing.	

    val schemeColonIndex: Option[Int] = indexOf(url, ':', startIndex, endIndex).filter(_ > 0)
    val urlScheme: Option[String] = schemeColonIndex match {
      case Some(index) =>
        val scheme = url.substring(startIndex, endIndex)
        if (isValidScheme(scheme)) {
          startIndex = index + 1
          Some(scheme)
        } else None

      case None => None
    }

    // Section 2.4.3: Parsing the Network Location/Login
    //
    //   If the parse string begins with a double-slash "//", then the
    //   substring of characters after the double-slash and up to, but not
    //   including, the next slash "/" character is the network location/login
    //   (<net_loc>) of the URL. If no trailing slash "/" is present, the
    //   entire remaining parse string is assigned to <net_loc>. The double-
    //   slash and <net_loc> are removed from the parse string before
    //   continuing.
    //
    // Note: We also accept a question mark "?" or a semicolon ";" character as
    //       delimiters for the network location/login (<net_loc>) of the URL.
    var locationStartIndex: Option[Int] = None
    var locationEndIndex: Option[Int] = None

    if (url.startsWith("//", startIndex)) {
      locationStartIndex = Option(startIndex + 2)
      locationEndIndex = indexOf(url, '/', locationStartIndex.get, endIndex)
      //if the index is found then update startIndex
      for (index <- locationEndIndex) {
        startIndex = index
      }
    }

    // Section 2.4.4: Parsing the Query Information
    //
    //   If the parse string contains a question mark "?" character, then the
    //   substring after the first (left-most) question mark "?" and up to the
    //   end of the parse string is the <query> information. If the question
    //   mark is the last character, or no question mark is present, then the
    //   query information is empty. The matched substring, including the
    //   question mark character, is removed from the parse string before
    //   continuing.
    val questionMarkIndex = indexOf(url, '?', startIndex, endIndex)
    val urlQuery = questionMarkIndex.map { questionIndex =>
      if (locationStartIndex.isDefined && locationEndIndex.isDefined) {
        // The substring of characters after the double-slash and up to, but not
        // including, the question mark "?" character is the network location/login
        // (<net_loc>) of the URL.
        locationEndIndex = questionMarkIndex
        startIndex = questionIndex
      }
      val urlQuery_ = url.substring(questionIndex + 1, endIndex)
      endIndex = questionIndex
      urlQuery_
    }

    // Section 2.4.5: Parsing the Parameters
    //
    //   If the parse string contains a semicolon ";" character, then the
    //   substring after the first (left-most) semicolon ";" and up to the end
    //   of the parse string is the parameters (<params>). If the semicolon
    //   is the last character, or no semicolon is present, then <params> is
    //   empty. The matched substring, including the semicolon character, is
    //   removed from the parse string before continuing.

    val semicolonIndex = indexOf(url, ';', startIndex, endIndex)
    val urlParameters = semicolonIndex.map { colonIndex =>
      if (locationStartIndex.isDefined && locationEndIndex.isDefined) {
        // The substring of characters after the double-slash and up to, but not
        // including, the semicolon ";" character is the network location/login
        // (<net_loc>) of the URL.
        locationEndIndex = semicolonIndex
        startIndex = colonIndex
      }
      val urlParameters_ = url.substring(colonIndex + 1, endIndex)
      endIndex = colonIndex
      urlParameters_
    }

    // Section 2.4.6: Parsing the Path
    //
    //   After the above steps, all that is left of the parse string is the
    //   URL <path> and the slash "/" that may precede it. Even though the
    //   initial slash is not part of the URL path, the parser must remember
    //   whether or not it was present so that later processes can
    //   differentiate between relative and absolute paths. Often this is
    //   done by simply storing the preceding slash along with the path.
    val urlPath: Option[String] =
      if (locationStartIndex.isDefined && locationEndIndex.isDefined) {
        // The entire remaining parse string is assigned to the network
        // location/login (<net_loc>) of the URL.
        locationEndIndex = Option(endIndex);
        None
      } else if (startIndex < endIndex) {
        Option(url.substring(startIndex, endIndex))
      } else
        None

    val urlLocation: Option[String] =
      if (locationStartIndex.isDefined && locationEndIndex.isDefined) {
        Option(url.substring(locationStartIndex.get, locationEndIndex.get))
      } else
        None

    Url(urlScheme, urlLocation, urlPath, urlParameters, urlQuery, urlFragment)
  }

  /**
   * @param scheme
   * @return is the scheme given is valid
   */
  private def isValidScheme(scheme: String): Boolean =
    scheme.length() match {
      case length if (length < 1) =>
        false

      case length if (!scheme(0).isLetter) =>
        false

      case length if (length > 1) =>
        if (scheme.exists(c => !c.isLetterOrDigit && c != '.' && c != '+' && c != '-'))
          false
        else true
    }

  /**
   * @param str
   * @param searchChar
   * @param startIndex
   * @param endIndex
   * @return get the first occurrence of the searchChar in str from start to end index
   */
  private def indexOf(str: String, searchChar: Char, startIndex: Int, endIndex: Int): Option[Int] = {

    val values = for (i <- startIndex to endIndex if (str(i) == searchChar)) yield i

    val min = values./:(Int.MaxValue)((a, b) => if (a < b) a else b)

    if (min == Int.MaxValue) None
    else Some(min)
  }
}

case class Url(scheme: Option[String], location: Option[String], path: Option[String],
  parameters: Option[String], query: Option[String], fragment: Option[String])