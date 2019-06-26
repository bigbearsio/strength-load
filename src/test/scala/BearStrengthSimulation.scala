import io.gatling.core.Predef.{jsonPath, _}
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.session.{Expression, Session}
import io.gatling.http.Predef._

import scala.util.Random
import scala.concurrent.duration._


class BearStrengthSimulation extends Simulation {

  val httpProtocol = http
    .baseUrl("http://localhost:3000")
    .header("Content-type","application/json")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Buying Tickets")
    .asLongAsDuring(
      session => !session("hasTicket").asOption[Boolean].getOrElse(false),
      2 minutes
    ) {
      exec( getRemainingSeat() )
      //TODO: Make randomSeat() exit gracefully if the seatings are exhausted
      .exec( randomSeat() )
      .exec( book() )
      //TODO: Pause with random time - AND/OR -
      //TODO: Use randomSwitch() to proportinally cancel or drops some of the booked tickets
      .exec(
        //TODO: Check if we confirm within a given-time, if not try again
        //TODO: Apply tolerance to clock skew between gatling and server
        confirm()
      )
      .exitHereIfFailed
    }


  setUp(scn.inject(atOnceUsers(400)).protocols(httpProtocol))

  def getRemainingSeat(): ActionBuilder = {
    http("getRemainingSeats")
      .get("/remaining")
      .check(
        jsonPath("$.unconfimedTicketsCount").ofType[Int].gte(0).saveAs("unconfimedTicketsCount"),
        jsonPath("$.seats[*]").ofType[String].findAll.saveAs("seats")
      )
  }

  def randomSeat(): Expression[Session] = {
    session => {
      val seats = session("seats").as[Vector[String]]
      val bookingSeat = seats(Random.nextInt(seats.size))
      session.set("bookingSeat", bookingSeat)
    }
  }

  def book(): ActionBuilder = {
    http("book")
      .post("/book")
      .body(StringBody("""{ "seat": "${bookingSeat}" }""")).asJson
      .check(
        jsonPath("$.success").ofType[Boolean].is(true),
        jsonPath("$.seat").ofType[String].is("${bookingSeat}"),
        jsonPath("$.reserve_expired_time").ofType[Long].saveAs("reserveExpiredTime")
      )
  }

  def confirm(): ActionBuilder = {
    http("confirm")
      .post("/confirm")
      .body(StringBody("""{ "seat": "${bookingSeat}" }""")).asJson
      .check(
        jsonPath("$.success").ofType[Boolean].is(true).saveAs("hasTicket"),
        //TODO: Write to log file confirm (clientID, seat, time) for aggregator to check
      )
  }
}

