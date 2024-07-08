package gl_com


import gl_com.CatalogPage.scnCatalog
import gl_com.HomePage.{host, scnHomePage}
import io.gatling.core.Predef._
import io.gatling.core.structure
import io.gatling.http.Predef._

import java.util.concurrent.atomic.AtomicLong

class Gl_com_Simulation extends Simulation {


  private val host: String = System.getProperty("urlCible", "https://www.galerieslafayette.com")

  val httpProtocol = http
    .baseUrl( host )
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
    .acceptEncodingHeader("gzip, deflate")
    .acceptLanguageHeader("fr,fr-FR;q=0.8,en-US;q=0.5,en;q=0.3")
    .userAgentHeader("TESTS-DE-PERF")
  //  .header("GL_DATA", "jojoijsdindoisdjpsidnsoidsjoidsdoisdidsoddqo2")


  val scnHomePage = scenario("Home Page").exec(HomePage.scnHomePage)
  val scnCatalog = scenario("Catalog").exec(CatalogPage.scnCatalog)
  val scnProduct = scenario("Product").exec(ProductPage.scnProduct)

  setUp(
      scnHomePage.inject(atOnceUsers(1)),
      scnCatalog.inject(atOnceUsers(1)),
      scnProduct.inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
