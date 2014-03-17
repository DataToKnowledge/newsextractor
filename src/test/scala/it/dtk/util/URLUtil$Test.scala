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

    val res1 = URLUtil getDomainName "http://www.example.com:80/bar.html"
    res1 should be a 'success
    res1.get should be ("example.com")
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

    val testUrls = Array(
      "http://www.baritoday.it/cronaca/ciccio-cappuCCio.html",
      "http://www.baritoday.it/cronaca/?arg1=va,lue",
      "http://www.baritoday.it/cronaca/index.php?arg1=va,lue",
      "http://www.example.com/?q=a+b",
      "http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037",
      "http://hostname.com",
      "http://HOSTNAME.com",
      "http://www.example.com",
      "http://www.example.com/index.html?name=test&rame=base#123",
      "http://www.example.com/~username/",
      "http://www.example.com:90/bar.html",
      "http://corrieredelmezzogiorno.corriere.it/bari/notizie/cronaca/2012/19-maggio-2012/diretta|-profumo-colpiti-cuore-201253003150.shtml",
      "http://www.example.com/index.html?&#123",
      "http://www.example.com/index.html?&",
      "http://www.example.com/index.html?"
      //"http://www.example.com/display?category=foo/bar+baz",
      //"http://www.example.com/display?category=foo%2Fbar%2Bbaz",
      //"http://www.example.com:80/bar.html"
      //"http://www.example.com//A//B/index.html",
      //"http://www.example.com/index.html?&x=y",
      //"http://www.example.com/../../a.html",
      //"http://www.example.com/../a/b/../c/./d.html",
      //"http://foo.bar.com?baz=1",
      //"http://www.example.com/index.html?&c=d&e=f&a=b",
      //"http://www.example.com/index.html?q=a b",
      //"http://www.example.com/search?width=100%&height=100%"
    )
    val correctUrls = Array(
      "http://www.baritoday.it/cronaca/ciccio-cappuccio.html",
      "http://www.baritoday.it/cronaca/?arg1=va%2Clue",
      "http://www.baritoday.it/cronaca/index.php?arg1=va%2Clue",
      "http://www.example.com/?q=a%2Bb",
      "http://somedomain.com/uploads/1/0/2/5/10259653/6199347.jpg?1325154037",
      "http://hostname.com/",
      "http://hostname.com/",
      "http://www.example.com/",
      "http://www.example.com/index.html?name=test&rame=base",
      "http://www.example.com/~username/",
      "http://www.example.com:90/bar.html",
      "http://corrieredelmezzogiorno.corriere.it/bari/notizie/cronaca/2012/19-maggio-2012/diretta%7C-profumo-colpiti-cuore-201253003150.shtml",
      "http://www.example.com/index.html",
      "http://www.example.com/index.html",
      "http://www.example.com/index.html"
      //"http://www.example.com/display?category=foo%2Fbar%2Bbaz",
      //"http://www.example.com/display?category=foo%2fbar%2bbaz",  // Check case
      //"http://www.example.com/bar.html"
      //"http://www.example.com/A/B/index.html",
      //"http://www.example.com/index.html?x=y",
      //"http://www.example.com/a.html",
      //"http://www.example.com/a/c/d.html",
      //"http://foo.bar.com/?baz=1",
      //"http://www.example.com/index.html?a=b&c=d&e=f",
      //"http://www.example.com/index.html?q=a%20b",
      //"http://www.example.com/search?height=100%&width=100%"
    )

    for((x,i) <- testUrls.view.zipWithIndex) {
      val result = URLUtil normalize testUrls.apply(i)
      result should be a 'success
      result.get should be (correctUrls.apply(i))
    }
  }

  it should "throw MalformedURLException if malformed URL is passed to normalize" in {
    val result = URLUtil normalize "baritoday"
    result should be a 'failure
    a [MalformedURLException] should be thrownBy result.get
  }

  it should "return requested resource" in {
    val testUrls1 = Array(
      "http://www.baritoday.it",
      "http://www.baritoday.it/",
      "http://www.baritoday.it/cronaca/4",
      "http://www.baritoday.it/cronaca/4/",
      "http://www.baritoday.it/cronaca/4/?arg1=val1")

    testUrls1 foreach (URLUtil getRequestedResource _ should be (None))

    val testUrls2 = Array(
      ("http://www.baritoday.it/index.html", Some("index.html")),
      ("http://www.baritoday.it/cronaca/4.php", Some("4.php")),
      ("http://www.baritoday.it/img/test.png", Some("test.png")),
      ("/index.html", Some("index.html")),
      ("index.html", Some("index.html")),
      ("/img/test.png", Some("test.png")),
      ("img/test.png", Some("test.png")),
      ("img/test.png", Some("test.png")),
      ("http://www.domain.it/index.php?option=com_flexicontent&view=items", Some("index.php")),
      ("http://domain.it/2012/diretta|-profumo-201253003150.shtml", Some("diretta|-profumo-201253003150.shtml"))
    )

    testUrls2 foreach (c => URLUtil getRequestedResource c._1 should be (c._2))
  }

  it should "return requested query arguments" in {
    val res1 = URLUtil getQueryArgs "http://www.senzacolonnenews.it/index.php?" +
      "option=com_flexicontent&view=items&cid=186:cronaca" +
      "&id=15645:malattie-neurologiche-passarella-direttore-del-corso-" +
      "&Itemid=3"
    val res2 = URLUtil getQueryArgs "http://test.it/?arg1=val1&arg2=val1&arg1=val2"
    val res3 = URLUtil getQueryArgs "http://test.it/index.php?arg1=val1&arg2=val1&arg1=val2"
    val res4 = URLUtil getQueryArgs "http://test.it/?"
    val res5 = URLUtil getQueryArgs "http://test.it/&"
    val res6 = URLUtil getQueryArgs "http://test.it/index.php?"
    val res7 = URLUtil getQueryArgs "http://test.it/index.php&"

    res1 shouldBe a [Some[_]]
    res2 shouldBe a [Some[_]]
    res3 shouldBe a [Some[_]]
    res4 should be (None)
    res5 should be (None)
    res6 should be (None)
    res7 should be (None)

    res1.get.size should be (5)
    res2.get.size should be (2)
    res3.get.size should be (2)
  }

  it should "compose base URL and relative path and normalize it" in {
    val baseUrl = "http://www.baritoday.it"
    val relativePath = "cronaca/ciccio-cappuccio.html"

    val res1 = URLUtil normalize (baseUrl + "/", "/" + relativePath)
    val res2 = URLUtil normalize (baseUrl, "/" + relativePath)
    val res3 = URLUtil normalize (baseUrl + "/", relativePath)
    val res4 = URLUtil normalize (baseUrl, relativePath)
    val res5 = URLUtil normalize ("http://www.senzacolonnenews.it/", "/cronaca/item/3135-drammatico-incidente-a-surbo-un-auto-entra-in-una-casa-morti-due-ragazzi,-feriti-altri-due-potrebbero-essere-brindisini.html")
    val res6 = URLUtil normalize ("http://www.senzacolonnenews.it/", "/index.php?option=com_flexicontent&view=items&cid=186:cronaca&id=15645:malattie-neurologiche-passarella-direttore-del-corso-&Itemid=3")

    res1 should be a 'success
    res2 should be a 'success
    res3 should be a 'success
    res4 should be a 'success
    res5 should be a 'success
    res6 should be a 'success

    res1.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
    res2.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
    res3.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
    res4.get should be ("http://www.baritoday.it/cronaca/ciccio-cappuccio.html")
    res5.get should be ("http://www.senzacolonnenews.it/cronaca/item/3135-drammatico-incidente-a-surbo-un-auto-entra-in-una-casa-morti-due-ragazzi%2C-feriti-altri-due-potrebbero-essere-brindisini.html")
    res6.get should be ("http://www.senzacolonnenews.it/index.php?option=com_flexicontent&id=15645%3Amalattie-neurologiche-passarella-direttore-del-corso-&cid=186%3Acronaca&itemid=3&view=items")
  }

  it should "encode URL if contains invalid characters" in {
    val invalidUrl1 = "http://www.senzacolonnenews.it/cronaca/item/3135-drammatico-incidente-a-surbo-un-auto-entra-in-una-casa-morti-due-ragazzi,-feriti-altri-due-potrebbero-essere-brindisini.html"
    val validUrl1 = "http://www.senzacolonnenews.it/cronaca/item/3135-drammatico-incidente-a-surbo-un-auto-entra-in-una-casa-morti-due-ragazzi%2C-feriti-altri-due-potrebbero-essere-brindisini.html"
    val invalidUrl2 = "http://www.senzacolonnenews.it/index.php?option=com_flexicontent&view=items&cid=186:cronaca&id=15645:malattie-neurologiche-passarella-direttore-del-corso-&Itemid=3"
    val validUrl2 = "http://www.senzacolonnenews.it/index.php?option=com_flexicontent&id=15645%3Amalattie-neurologiche-passarella-direttore-del-corso-&cid=186%3Acronaca&itemid=3&view=items"
    val invalidUrl3 = "http://www.test.it/?arg1=ci|ao"
    val validUrl3 = "http://www.test.it/?arg1=ci%7Cao"
    val invalidUrl4 = "http://www.test.it/in,dex.php"
    val validUrl4 = "http://www.test.it/in%2Cdex.php"
    val invalidUrl5 = "http://www.test.it/boh/"
    val validUrl5 = "http://www.test.it/boh/"
    val invalidUrl6 = "http://corrieredelmezzogiorno.corriere.it/bari/notizie/cronaca/2012/19-maggio-2012/diretta|-profumo-colpiti-cuore-201253003150.shtml"
    val validUrl6 = "http://corrieredelmezzogiorno.corriere.it/bari/notizie/cronaca/2012/19-maggio-2012/diretta%7C-profumo-colpiti-cuore-201253003150.shtml"

    URLUtil encodeURL invalidUrl1 should be (validUrl1)
    URLUtil encodeURL invalidUrl2 should be (validUrl2)
    URLUtil encodeURL invalidUrl3 should be (validUrl3)
    URLUtil encodeURL invalidUrl4 should be (validUrl4)
    URLUtil encodeURL invalidUrl5 should be (validUrl5)
    URLUtil encodeURL invalidUrl6 should be (validUrl6)
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

