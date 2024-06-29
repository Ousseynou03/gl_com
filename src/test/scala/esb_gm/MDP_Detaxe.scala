package esb_gm


import io.gatling.core.Predef._
import io.gatling.core.structure
import io.gatling.http.Predef._

import java.util.concurrent.atomic.AtomicLong

class MDP_Detaxe extends Simulation {


  private val host: String = System.getProperty("urlCible", "http://esbpprd1.ty7:1608/ws/taxfreeCreation/taxfreecreation")

  val httpProtocol = http
    .baseUrl(host)
    .header("Content-Type", "application/json")
    .basicAuth("bornedetaxe", "pE4gg9M5")

  val idIdentite = new AtomicLong(System.currentTimeMillis())

  def identifiantUnique() : structure.ChainBuilder = {
    exec { session =>
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
          """
            |{
            |"identite" : "PASSP${idIdentite}",
            |"nationaliteIso3" : "FRA"
            |}
          """.stripMargin)).asJson
        .check(status.is(200))
      )
  }

  def creationClient :  structure.ChainBuilder = {
    exec(http("Création Client")
      .post("/clients")
      .body(StringBody(
        """
          |{
          |  "identite": "PASSP${idIdentite}",
          |  "nom": "JOHN",
          |  "prenom": "DOE",
          |  "nationalite": 10074,
          |  "naissance": "01/01/1990",
          |  "expirationPasseport": "01/01/2030",
          |  "adresse1": "55 PABLO STREET",
          |  "adresse2": "",
          |  "adresseCodePostal": "10001",
          |  "adresseVille": "NEWYORK",
          |  "etat": null,
          |  "paysResidence": 10230,
          |  "mail": "MTABOSA@GALERIESLAFAYETTE.COM",
          |  "optinEmail": false,
          |  "langueCommunication": 1000,
          |  "idMagasin": "03050",
          |  "civilite": "M"
          |}
        """.stripMargin)).asJson
      .check(jsonPath("$.idClient").saveAs("idClient"))
      .check(status.is(200))
    )
  }

  // Modification Client
  def modifClient() : structure.ChainBuilder = {
    exec(http("Modification Client")
      .post("/clients/modification")
      .body(StringBody(
        """
          |{
          |  "id": ${idClient},
          |  "identite": "PASSP${idIdentite}",
          |  "expirationPasseport": "01/01/2030",
          |  "civilite": "M",
          |  "nom": "JOHN",
          |  "prenom": "DOE",
          |  "naissance": "01/01/1990",
          |  "adresse1": "60 PABLO STREET",
          |  "adresse2": null,
          |  "adresse3": null,
          |  "adresse4": null,
          |  "adresseCodePostal": "10001",
          |  "adresseVille": "NEWYORK",
          |  "etat": 5016,
          |  "paysResidence": 10230,
          |  "nationalite": 10074,
          |  "nomPaysResidence": "ÉTATS-UNIS",
          |  "nomPaysNationalite": "FRANCE",
          |  "mail": "MTABOSA@GALERIESLAFAYETTE.COM",
          |  "optinEmail": true,
          |  "telephone": null,
          |  "langueCommunication": 1001,
          |  "indicatifAvecISO": null,
          |  "indicatifTelephonique": null,
          |  "optinCommercial": false
          |}
        """.stripMargin)).asJson
      .check(jsonPath("$.idClient").saveAs("idClientModif"))
      .check(status.is(200))
    )
  }


  // Initialisation d'un BVE
  def initBVE() : structure.ChainBuilder = {
    exec(http("Initialisation BVE")
      .post("/bve/creer")
      .body(StringBody(
        """
          |{
          |  "idClient": ${idClientModif},
          |  "idBorne": 1,
          |  "username": "borne",
          |  "idMagasin": 3050
          |}
          |
        """.stripMargin)).asJson
      .check(jsonPath("$.idBve").saveAs("idBve"))
      .check(status.is(200))
    )
      .exec { session =>
        println("idBve :" + session("idBve").as[String])
        session
      }
  }


  // Choix du mode de Remboursement
  def modeRemboursement() : structure.ChainBuilder = {
    exec(http("Choix mode Remboursement")
      .post("/remboursements/choix-remboursement-initial")
      .body(StringBody(
        """
          |{
          |  "idsBve": [${idBve}],
          |  "idClient": ${idClientModif},
          |  "modeCode": "CB_DIFF",
          |  "typeCode": "TYPE_DIFF"
          |}
        """.stripMargin)).asJson
      .check(status.is(200))
    )
  }

  // Envoie de l'emprunt CB
  def envoieEmpruntCb() : structure.ChainBuilder = {
    exec(http("Envoie Emprunt CB")
      .post("/remboursements/empreinte-cb")
      .body(StringBody(
        """
          |{
          |  "refundId": [
          |    {
          |      "idBve": ${idBve},
          |      "montant": 0
          |    }
          |  ],
          |  "empreinteCB": "xxxxxxxxxxx",
          |  "typeCarte": "VI",
          |  "flagConvertibilite": "2",
          |  "sixPremiers": "123456",
          |  "quatreDerniers": "1234"
          |}
        """.stripMargin)).asJson
      .check(status.is(200))
    )
  }

  // Ajout d'un ticket au BVE
  def ajoutTicketBVE() : structure.ChainBuilder = {
    exec(http("Ajout ticket au BVE")
      .post("/tickets/ajout")
      .body(StringBody(
        """
          |{
          |  "idsBve": [${idBve}],
          |  "idMagasin": 12,
          |  "codeBarresTicket": "403415100212"
          |}
        """.stripMargin)).asJson
      .check(jsonPath("$.idBve").saveAs("idBveModif"))
      .check(status.is(200))
    )
  }

  // Recap du BVE
  def recapBVE() : structure.ChainBuilder = {
    exec(http("Recap du BVE")
      .post("/bve/recap")
      .body(StringBody(
        """
          |{
          |  "idsBve": ${idBveModif},
          |}
        """.stripMargin)).asJson
      .check(status.is(200))
    )
  }

  // Validation du BVE
  def validationBVE() : structure.ChainBuilder = {
    exec(http("Validation du BVE")
      .post("/bve/emettre")
      .body(StringBody(
        """
          |{
          |  "idsBve": [${idBveModif}],
          |  "idMagasin": 12,
          |  "idBorne": 1,
          |  "username": "borne",
          |  "espaceDetaxe": "INDIV",
          |  "envoiMailBveDemat": "light",
          |  "signature": "<base64>",
          |  "signatureRembImm": null
          |}
          |
        """.stripMargin)).asJson
      .check(status.is(200))
    )
  }

  setUp(
    scenario("MDP Detaxe")
      .exec(identifiantUnique())
      .exec(creationClient)
      .exec(modifClient)
      .exec(initBVE)
      .exec(modeRemboursement)
      .exec(envoieEmpruntCb)
      .exec(ajoutTicketBVE)
      .exec(recapBVE)
      .exec(validationBVE)
      .inject(atOnceUsers(1))
  ).protocols(httpProtocol)
}
