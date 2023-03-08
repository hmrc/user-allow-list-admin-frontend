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

import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, equalToJson, put, urlMatching}
import com.github.tomakehurst.wiremock.http.Fault
import models.SetRequest
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.http.Status.{INTERNAL_SERVER_ERROR, OK}
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
}
