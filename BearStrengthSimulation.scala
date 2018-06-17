import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class BearStrengthSimulation extends Simulation {
  val httpConf = http
    .baseURL("http://localhost:3000") // Here is the root for all relative URLs
    .acceptHeader("application/json")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.8; rv:16.0) Gecko/20100101 Firefox/16.0")

  val scn = scenario("Scenario Name") // A scenario is a chain of requests and pauses
    .exec(http("getBookings")
      .get("/bookings"))
    .pause(1)
    

  setUp(scn.inject(atOnceUsers(1)).protocols(httpConf))
}