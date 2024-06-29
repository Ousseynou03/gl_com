package esb_gm

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class esb_gm_Simulation extends Simulation {


  private val host: String = System.getProperty("urlCible", "http://esbpprd1.ty7:1608/ws/taxfreeCreation/taxfreecreation")

  val httpProtocol = http
    .baseUrl(host)
    .header("Content-Type", "application/json")
    .basicAuth("bornedetaxe", "pE4gg9M5")
    //.header("Authorization","Basic Ym9ybmVkZXRheGU6cEU0Z2c5TTU=")
    //.header("Cookie", "JSESSIONID=697A862B0A1631BB669E898B6B795DEE.server01")

  val scnIdentifiantUnique = scenario("Scenario Identifiant Unique").exec(IdentifiantUnique.scnIdentifiantUnique)



  setUp(scnIdentifiantUnique.inject(atOnceUsers(1)).protocols(httpProtocol))

}
