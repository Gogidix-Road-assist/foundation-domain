import io.gatling.javaapi.core.CoreDsl._
import io.gatling.javaapi.core.CoreDsl._
import io.gatling.javaapi.core.http.HttpDsl._
import io.gatling.javaapi.core.chain.ChainDsl._
import scala.concurrent.duration._

import io.gatling.javaapi.core._

/**
 * Foundation Domain API Load Test
 *
 * Tests critical API endpoints under load
 */
class FoundationDomainLoadTest extends Simulation {

  // Target API base URL
  val baseUrl = System.getProperty("baseUrl", "http://localhost:8080")
  val tenantId = System.getProperty("tenantId", "default")
  val authToken = System.getProperty("authToken", "test-token")

  // HTTP Protocol Configuration
  val httpProtocol = http
    .baseUrl(baseUrl)
    .acceptHeader("application/json")
    .contentTypeHeader("application/json")
    .header("X-Tenant-ID", tenantId)
    .header("Authorization", s"Bearer $authToken")

  // Scenario 1: API Gateway Health Check
  val healthCheckScenario = scenario("Health Check")
    .exec(
      http("Health Check")
        .get("/actuator/health")
        .check(status.is(200))
    )

  // Scenario 2: Identity Service - Login
  val loginScenario = scenario("User Login")
    .exec(
      http("Login Request")
        .post("/api/identity/auth/login")
        .body(StringBody("""
          {"username":"testuser","password":"Test@123"}
        """))
        .check(status.in(200, 201, 401))
        .check(jsonPath("$.token").optional.saveAs("authToken"))
    )

  // Scenario 3: Tenant Service - Get Tenants
  val tenantScenario = scenario("Tenant Operations")
    .exec(
      http("Get Tenant")
        .get("/api/tenant/tenants")
        .header("X-Tenant-ID", tenantId)
        .check(status.is(200))
    )

  // Scenario 4: Configuration Service - Get Config
  val configScenario = scenario("Configuration Fetch")
    .exec(
      http("Get Configuration")
        .get("/api/config")
        .check(status.is(200))
    )

  // Scenario 5: Dashboard Analytics
  val dashboardScenario = scenario("Dashboard Analytics")
    .exec(
      http("Get Analytics Data")
        .get("/api/dashboard/analytics")
        .queryParam("from", "2024-01-01")
        .queryParam("to", "2024-12-31")
        .check(status.is(200))
    )

  // Scenario 6: Payment Service - Create Payment
  val paymentScenario = scenario("Payment Processing")
    .exec(
      http("Create Payment Intent")
        .post("/api/payment/intents")
        .body(StringBody("""
          {
            "amount": 10000,
            "currency": "USD",
            "payment_method": "pm_test_visa"
          }
        """))
        .check(status.in(200, 201, 400))
    )

  // Scenario 7: Geo Location Service
  val geoLocationScenario = scenario("Geo Location Lookup")
    .exec(
      http("Get Location from IP")
        .get("/api/geo/location/ip")
        .queryParam("ip", "8.8.8.8")
        .check(status.in(200, 404))
    )

  // Load Test Profile
  setUp(
    healthCheckScenario.inject(
      rampUsersPerSec(1).to(10).during(10.seconds),
      constantUsersPerSec(10).during(30.seconds),
      rampUsersPerSec(10).to(1).during(10.seconds)
    ).protocols(httpProtocol),

    loginScenario.inject(
      rampUsersPerSec(1).to(50).during(20.seconds),
      constantUsersPerSec(50).during(60.seconds),
      rampUsersPerSec(50).to(1).during(10.seconds)
    ).protocols(httpProtocol),

    tenantScenario.inject(
      rampUsersPerSec(1).to(30).during(15.seconds),
      constantUsersPerSec(30).during(45.seconds),
      rampUsersPerSec(30).to(1).during(10.seconds)
    ).protocols(httpProtocol),

    configScenario.inject(
      rampUsersPerSec(1).to(100).during(10.seconds),
      constantUsersPerSec(100).during(30.seconds),
      rampUsersPerSec(100).to(1).during(5.seconds)
    ).protocols(httpProtocol),

    dashboardScenario.inject(
      rampUsersPerSec(1).to(20).during(15.seconds),
      constantUsersPerSec(20).during(40.seconds),
      rampUsersPerSec(20).to(1).during(10.seconds)
    ).protocols(httpProtocol),

    paymentScenario.inject(
      rampUsersPerSec(1).to(25).during(10.seconds),
      constantUsersPerSec(25).during(50.seconds),
      rampUsersPerSec(25).to(1).during(10.seconds)
    ).protocols(httpProtocol),

    geoLocationScenario.inject(
      rampUsersPerSec(1).to(40).during(15.seconds),
      constantUsersPerSec(40).during(30.seconds),
      rampUsersPerSec(40).to(1).during(5.seconds)
    ).protocols(httpProtocol)
  ).assertions(
    global.responseTime.percentile3.lt(500), // 95% of requests under 500ms
    global.responseTime.percentile4.lt(1000), // 99% of requests under 1s
    global.successfulRequests.percent.gt(95) // 95% success rate
  )
}
