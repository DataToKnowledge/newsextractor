package it.dtk.util

import org.scalatest.{Matchers, FlatSpec}
import java.net.MalformedURLException

/**
 * Author: Michele Damiano Torelli
 * Project: NewsExtractor
 * Date: 17/02/14
 * Time: 11:33
 */
class URLUtil$Test extends FlatSpec with Matchers {
  "A URLUtils" should "return the domain name" in {
    urls foreach {
      url =>
        val domain = URLUtil.getDomainName(url)
        println(s"- URL: $url => Domain: $domain")

        domain should be ("baritoday.it")
    }
  }

  it should "throw MalformedURLException if malformed URL is passed to getDomainName" in {
    a [MalformedURLException] should be thrownBy {
      URLUtil.getDomainName("baritoday")
    }
  }

  it should "normalize a URL" in {
    normUrls1 foreach {
      url =>
        val normalizedUrl = URLUtil.normalize(url)
        println(s"- URL: $url => Normalized: $normalizedUrl")

        normalizedUrl should be ("http://www.baritoday.it/")
    }

    normUrls2 foreach {
      url =>
        val normalizedUrl = URLUtil.normalize(url)
        println(s"- URL: $url => Normalized: $normalizedUrl")

        normalizedUrl should be ("http://www.baritoday.it/pages/4/")
    }
  }

  it should "throw MalformedURLException if malformed URL is passed to normalize" in {
    a [MalformedURLException] should be thrownBy {
      URLUtil.normalize("baritoday")
    }
  }

  it should "check if a URL is absolute" in {
    absUrls foreach {
      url =>
        val isAbsolute = URLUtil.isAbsolute(url)
        println(s"- URL: $url => Is absolute: $isAbsolute")

        isAbsolute should be (true)
    }

    relUrls foreach {
      url =>
        val isAbsolute = URLUtil.isAbsolute(url)
        println(s"- URL: $url => Is absolute: $isAbsolute")

        isAbsolute should be (false)
    }
  }

  it should "check if a URL is relative" in {
    relUrls foreach {
      url =>
        val isRelative = URLUtil.isRelative(url)
        println(s"- URL: $url => Is relative: $isRelative")

        isRelative should be (true)
    }

    absUrls foreach {
      url =>
        val isRelative = URLUtil.isRelative(url)
        println(s"- URL: $url => Is relative: $isRelative")

        isRelative should be (false)
    }
  }

  it should "check if a URL is already normalized" in {
    normUrls1 ++ normUrls2 foreach {
      url =>
        val isNormalized = URLUtil.isNormalized(url)
        println(s"- URL: $url => Is normalized: $isNormalized")

        isNormalized should be (false)
    }

    URLUtil.isNormalized("http://www.baritoday.it/") should be (true)
    URLUtil.isNormalized("https://www.baritoday.it/") should be (true)
    URLUtil.isNormalized("http://www.baritoday.it/notizia-a-caso/") should be (true)
    URLUtil.isNormalized("https://www.baritoday.it/notizia-a-caso/") should be (true)
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
    "baritoday.it/notizia-a-caso/"
  )
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
    "https://baritoday.it/notizia-a-caso/",
    "/notizie/notizia-a-caso",
    "/notizie/notizia-a-caso/",
    "//notizie/notizia-a-caso",
    "//notizie/notizia-a-caso"
  )
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
    "../notizie/notizia-a-caso/"
  )
  val normUrls1 = List(
    "http://www.baritoday.it",
    "hTtp://Www.BARITodaY.iT",
    "http://www.baritoday.it/#events",
    "www.baritoday.it",
    "www.baritoday.it/"
  )
  val normUrls2 = List(
    "http://www.baritoday.it/pages/4",
    "hTtp://Www.BARITodaY.iT/paGes/4",
    "http://www.baritoday.it/pages/4/#about",
    "www.baritoday.it/pages/4",
    "www.baritoday.it/pages/4/"
  )
}
