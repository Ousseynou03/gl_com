package gl_com

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.language.postfixOps

object CatalogPage {

  private val  TpsPause : Int = System.getProperty("tpsPause", "720").toInt
  private val  NbreIter : Int = System.getProperty("nbIter", "20").toInt

  private val FichierPath: String="src/test/resources/data"
  private val FichierData: String = "urlC.csv"

  val jddurlC = csv(FichierPath + FichierData ).circular

  val scnCatalog = scenario("Catalog")
    .repeat(NbreIter ) {
      feed(jddurlC)
        .exec(http("/Catalog")
          .get("${url}")
          .check(status.is(200)))
        .pause( TpsPause seconds)
    }


}
