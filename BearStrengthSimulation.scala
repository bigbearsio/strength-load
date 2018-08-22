import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BearStrengthSimulation extends Simulation {
  val httpConf = http
    .baseURL("http://localhost:3000") // Here is the root for all relative URLs
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Book a ticket")
    .exec(http("Get Remaining Seats")
      .get("/remaining")
      .check(jsonPath("$.seats[0]").saveAs("seatId"))
      .check(jsonPath("$.unconfimedTicketsCount").saveAs("unconfimedTicketsCount"))
    ).exec(http("Book the Seat")
      .post("/book")
      .body(StringBody("""{ "seat": "${seatId}" }""")).asJSON
      .check(jsonPath("$.success").ofType[Boolean].is(true))
    ).exec(http("Confirm Ticket")
      .post("/confirm")
      .body(StringBody("""{ "seat": "${seatId}" }""")).asJSON
      .check(jsonPath("$.success").ofType[Boolean].is(true))
    )
    

  setUp(scn.inject(atOnceUsers(10)).protocols(httpConf))
}

/*
Maybe useful snippets

.exec(session => {
      val maybeId = session.get("seatId").asOption[String]
      println(maybeId.getOrElse("COULD NOT FIND SEAT ID"))
      session
    })
*/