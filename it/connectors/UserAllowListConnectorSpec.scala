/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package connectors

import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.http.Fault
import models._
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import uk.gov.hmrc.http.HeaderCarrier
import util.WireMockHelper

class UserAllowListConnectorSpec extends AnyFreeSpec with Matchers with ScalaFutures with IntegrationPatience with WireMockHelper {

  private lazy val app: Application =
    GuiceApplicationBuilder()
      .configure(
        "microservice.services.user-allow-list.port" -> server.port(),
      )
      .build()

  private lazy val connector = app.injector.instanceOf[UserAllowListConnector]

  ".set" - {

    val url = "/user-allow-list/admin/service/feature"
    val hc = HeaderCarrier()
    val request = SetRequest(Set("a", "b"))

    "must return successfully when the server responds with OK" in {

      server.stubFor(
        put(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.set("service", "feature", Set("a", "b"))(hc).futureValue
    }

    "must fail when the server responds with anything else" in {

      server.stubFor(
        put(urlMatching(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.set("service", "feature", Set("a", "b"))(hc).failed.futureValue
    }

    "must fail when the server connection fails" in {

      server.stubFor(
        put(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.set("service", "feature", Set("a", "b"))(hc).failed.futureValue
    }
  }

  ".delete" - {

    val url = "/user-allow-list/admin/service/feature"
    val hc = HeaderCarrier()
    val request = DeleteRequest(Set("a", "b"))

    "must return successfully when the server responds with OK" in {

      server.stubFor(
        delete(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.delete("service", "feature", Set("a", "b"))(hc).futureValue
    }

    "must fail when the server responds with anything else" in {

      server.stubFor(
        delete(urlMatching(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.delete("service", "feature", Set("a", "b"))(hc).failed.futureValue
    }

    "must fail when the server connection fails" in {

      server.stubFor(
        delete(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.delete("service", "feature", Set("a", "b"))(hc).failed.futureValue
    }
  }

  ".check" - {

    val url = "/user-allow-list/admin/service/feature/check"
    val hc = HeaderCarrier()
    val request = CheckRequest("foobar")

    "must return true when the server responds with OK" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.check("service", "feature", "foobar")(hc).futureValue mustBe true
    }

    "must return false when the server responds with NOT_FOUND" in {

      server.stubFor(
        post(urlMatching(url))
          .withRequestBody(equalToJson(Json.stringify(Json.toJson(request))))
          .willReturn(aResponse().withStatus(NOT_FOUND))
      )

      connector.check("service", "feature", "foobar")(hc).futureValue mustBe false
    }

    "must fail when the server responds with anything else" in {

      server.stubFor(
        post(urlMatching(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.check("service", "feature", "foobar")(hc).failed.futureValue
    }

    "must fail when the server connection fails" in {

      server.stubFor(
        post(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.check("service", "feature", "foobar")(hc).failed.futureValue
    }
  }

  ".clear" - {

    val url = "/user-allow-list/admin/service/feature/clear"
    val hc = HeaderCarrier()

    "must return successfully when the server responds with OK" in {

      server.stubFor(
        post(urlMatching(url))
          .willReturn(aResponse().withStatus(OK))
      )

      connector.clear("service", "feature")(hc).futureValue
    }

    "must fail when the server responds with anything else" in {

      server.stubFor(
        post(urlMatching(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.clear("service", "feature")(hc).failed.futureValue
    }

    "must fail when the server connection fails" in {

      server.stubFor(
        post(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.clear("service", "feature")(hc).failed.futureValue
    }
  }

  ".summary" - {

    val url = "/user-allow-list/admin/service/summary"
    val hc = HeaderCarrier()

    "must return successfully when the server responds with OK" in {

      val response = SummaryResponse(
        summaries = Seq(
          Summary("foobar", 2),
          Summary("quux", 3)
        )
      )

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withStatus(OK).withBody(Json.stringify(Json.toJson(response))))
      )

      connector.summary("service")(hc).futureValue mustEqual response.summaries
    }

    "must fail when the server responds with anything else" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withStatus(INTERNAL_SERVER_ERROR))
      )

      connector.summary("service")(hc).failed.futureValue
    }

    "must fail when the server connection fails" in {

      server.stubFor(
        get(urlMatching(url))
          .willReturn(aResponse().withFault(Fault.RANDOM_DATA_THEN_CLOSE))
      )

      connector.summary("service")(hc).failed.futureValue
    }
  }
}
