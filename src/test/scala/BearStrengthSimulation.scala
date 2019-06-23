import io.gatling.core.Predef.{jsonPath, _}
import io.gatling.http.Predef._

import scala.util.Random

class BearStrengthSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:3000") // Here is the root for all relative URLs
    .header("Content-type","application/json")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Buying Tickets") // A scenario is a chain of requests and pauses
    //TODO: Loop

    .exec(
      http("getRemainingSeats")
      .get("/remaining")
      .check(
        status.is(200),
        jsonPath("$.unconfimedTicketsCount").ofType[Int].gte(0).saveAs("unconfimedTicketsCount"),
        jsonPath("$.seats[*]").ofType[String].findAll.saveAs("seats")
      )
    )
    .exec( session => {
      val seats = session("seats").as[Vector[String]]
      val bookingSeat = seats(Random.nextInt(seats.size))
      println(bookingSeat)
      session.set("bookingSeat", bookingSeat)
    })
    .exec(
      http("book")
        .post("/book")
        .body(StringBody("""{ "seat": "${bookingSeat}" }""")).asJson
        .check(
          status.is(200),
          jsonPath("$.success").ofType[Boolean].is(true),
          jsonPath("$.seat").ofType[String].is("${bookingSeat}"),
          jsonPath("$.reserve_expired_time").ofType[Long].saveAs("reserveExpiredTime")
        )
    )
    //TODO: Use randomSwitch() to proportinally cancel or drops some of the booked tickets
    .exec(
      //TODO: Check if we confirm within a given-time, if not try again
      //TODO: Apply tolerance to clock skew between gatling and server

      http("confirm")
        .post("/confirm")
        .body(StringBody("""{ "seat": "${bookingSeat}" }""")).asJson
        .check(
          status.is(200),
          jsonPath("$.success").ofType[Boolean].is(true),
          //TODO: Write to log file confirm (clientID, seat, time) for aggregator to check
        )
    )

  setUp(scn.inject(atOnceUsers(1)).protocols(httpProtocol))
}