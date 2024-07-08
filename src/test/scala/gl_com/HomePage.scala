package gl_com

import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

import scala.language.postfixOps

object HomePage {

  private val  TpsPause : Int = System.getProperty("tpsPause", "720").toInt
  private val  NbreIter : Int = System.getProperty("nbIter", "20").toInt





  val scnHomePage = scenario("Home")
    .repeat( NbreIter ) {
      exec(	http("/Home")
        .get("/")
        .check(status.is(200))
      )
        .pause( TpsPause seconds)
    } //.repeat


}
