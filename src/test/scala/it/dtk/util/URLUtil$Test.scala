package it.dtk.util

import org.scalatest.{ Matchers, FlatSpec }
import java.net.MalformedURLException


/**
 * URL utilities ScalaTest class.
 *
 * @author Michele Damiano Torelli <daniele@datatoknowledge.it>
 */
class URLUtil$Test extends FlatSpec with Matchers {
    
  "A URLUtils" should "return the domain name" in {
    urls foreach {
      url =>
        val result = URLUtil getDomainName url
        result should be a 'success
        result.get should be ("baritoday.it")
    }
  }

  it should "throw MalformedURLException if malformed URL is passed to getDomainName" in {
    val result = URLUtil getDomainName "baritoday"
    result should be a 'failure
    a [MalformedURLException] should be thrownBy result.get
  }

  it should "normalize a URL" in {
    normUrls1 foreach {
      url =>
        val result = URLUtil normalize url
        result should be a 'success
        result.get should be ("http://www.baritoday.it/")
    }

    normUrls2 foreach {
      url =>
        val result = URLUtil normalize url
        result should be a 'success
        result.get should be ("http://www.baritoday.it/pages/4/")
    }

    val result = URLUtil normalize "http://www.baritoday.it/cronaca/ciccio-cappuCCio.html"
    result should be a 'success
    result.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
  }

  it should "throw MalformedURLException if malformed URL is passed to normalize" in {
    val result = URLUtil normalize "baritoday"
    result should be a 'failure
    a [MalformedURLException] should be thrownBy result.get
  }

  it should "return requested resource" in {
    URLUtil getRequestedResource "http://www.baritoday.it" should be (None)
    URLUtil getRequestedResource "http://www.baritoday.it/" should be (None)
    URLUtil getRequestedResource "http://www.baritoday.it/cronaca/4" should be (None)
    URLUtil getRequestedResource "http://www.baritoday.it/cronaca/4/" should be (None)

    URLUtil getRequestedResource "http://www.baritoday.it/index.html" should be (Some("index.html"))
    URLUtil getRequestedResource "http://www.baritoday.it/cronaca/4.php" should be (Some("4.php"))
    URLUtil getRequestedResource "http://www.baritoday.it/img/test.png" should be (Some("test.png"))
    URLUtil getRequestedResource "/index.html" should be (Some("index.html"))
    URLUtil getRequestedResource "index.html" should be (Some("index.html"))
    URLUtil getRequestedResource "/img/test.png" should be (Some("test.png"))
    URLUtil getRequestedResource "img/test.png" should be (Some("test.png"))
  }

  it should "compose base URL and relative path and normalize it" in {
    val baseUrl = "http://www.baritoday.it"
    val relativePath = "cronaca/ciccio-cappuccio.html"

    val res1 = URLUtil normalize (baseUrl + "/", "/" + relativePath)
    val res2 = URLUtil normalize (baseUrl, "/" + relativePath)
    val res3 = URLUtil normalize (baseUrl + "/", relativePath)
    val res4 = URLUtil normalize (baseUrl, relativePath)

    res1 should be a 'success
    res2 should be a 'success
    res3 should be a 'success
    res4 should be a 'success

    res1.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
    res2.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
    res3.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
    res4.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
  }

  it should "check if a URL is absolute" in {
    absUrls foreach (URLUtil isAbsolute _ should be (true))
    relUrls foreach (URLUtil isAbsolute _ should be (false))
  }

  it should "check if a URL is relative" in {
    relUrls foreach (URLUtil isRelative _ should be (true))
    absUrls foreach (URLUtil isRelative _ should be (false))
  }

  it should "check if a URL is already normalized" in {
    normUrls1 ++ normUrls2 foreach (URLUtil isNormalized _ should be (false))
    URLUtil isNormalized "http://www.baritoday.it/" should be (true)
    URLUtil isNormalized "https://www.baritoday.it/" should be (true)
    URLUtil isNormalized "http://www.baritoday.it/notizia-a-caso/" should be (true)
    URLUtil isNormalized "https://www.baritoday.it/notizia-a-caso/" should be (true)
  }
  
    val urls = List(
    "http://www.baritoday.it",
    "http://www.baritoday.it/",
    "https://www.baritoday.it",
    "https://www.baritoday.it/",
    "https://www.baritoday.it/#events",
    "http://www.baritoday.it/notizia-a-caso",
    "http://www.baritoday.it/notizia-a-caso/",
    "https://www.baritoday.it/notizia-a-caso",
    "https://www.baritoday.it/notizia-a-caso/",
    "http://baritoday.it/notizia-a-caso",
    "http://baritoday.it/notizia-a-caso/",
    "https://baritoday.it/notizia-a-caso",
    "https://baritoday.it/notizia-a-caso/",
    "baritoday.it/notizia-a-caso",
    "baritoday.it/notizia-a-caso/")

  val absUrls = List(
    "http://www.baritoday.it",
    "http://www.baritoday.it/",
    "https://www.baritoday.it",
    "https://www.baritoday.it/",
    "http://www.baritoday.it/notizia-a-caso",
    "http://www.baritoday.it/notizia-a-caso/",
    "https://www.baritoday.it/notizia-a-caso",
    "https://www.baritoday.it/notizia-a-caso/",
    "http://baritoday.it/notizia-a-caso",
    "http://baritoday.it/notizia-a-caso/",
    "https://baritoday.it/notizia-a-caso",
    "https://baritoday.it/notizia-a-caso/")

  val relUrls = List(
    "baritoday.it/notizia-a-caso",
    "baritoday.it/notizia-a-caso/",
    "notizia-a-caso",
    "notizia-a-caso/",
    "./notizia-a-caso",
    "./notizia-a-caso/",
    "../notizia-a-caso",
    "../notizia-a-caso/",
    "../notizie/notizia-a-caso",
    "../notizie/notizia-a-caso/",
    "/notizie/notizia-a-caso",
    "/notizie/notizia-a-caso/",
    "//notizie/notizia-a-caso",
    "//notizie/notizia-a-caso")

  val normUrls1 = List(
    "http://www.baritoday.it",
    "hTtp://Www.BARITodaY.iT",
    "http://www.baritoday.it/#events",
    "www.baritoday.it",
    "www.baritoday.it/")

  val normUrls2 = List(
    "http://www.baritoday.it/pages/4",
    "hTtp://Www.BARITodaY.iT/paGes/4",
    "http://www.baritoday.it/pages/4/#about",
    "www.baritoday.it/pages/4",
    "www.baritoday.it/pages/4/")
}

