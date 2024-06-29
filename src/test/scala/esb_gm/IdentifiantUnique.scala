package esb_gm

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.util.concurrent.atomic.AtomicLong

object IdentifiantUnique {


  val idIdentite = new AtomicLong(System.currentTimeMillis())

  val scnIdentifiantUnique = scenario("Identifiant Unique")
    .exec { session =>
      val idIdentiteInc = idIdentite.incrementAndGet()
      session.set("idIdentite", idIdentiteInc)
    }
    .exec { session =>
      println("idIdentite :" + session("idIdentite").as[String])
      session
    }
    .exec(http("Identifiant Unique")
      .post("/clients/unique")
      .body(StringBody(
        s"""
          |{
          |"identite" : "PASSP${idIdentite}",
          |"nationaliteIso3" : "FRA"
          |}
          """.stripMargin)).asJson
      .check(status.is(200)))
    .pause(3)

}
