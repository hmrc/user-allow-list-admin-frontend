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

package controllers

import connectors.UserAllowListConnector
import forms.AllowListEntriesFormProvider
import models.Done
import org.mockito.ArgumentMatchers.{any, eq as eqTo}
import org.mockito.Mockito
import org.mockito.Mockito.{times, verify, when, never}
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.{BeforeAndAfterEach, OptionValues}
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.{Messages, MessagesApi}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.FakeRequest
import play.api.test.Helpers.*
import uk.gov.hmrc.internalauth.client.*
import uk.gov.hmrc.internalauth.client.test.{FrontendAuthComponentsStub, StubBehaviour}
import views.html.UserAllowListDeleteView

import scala.concurrent.ExecutionContext.global
import scala.concurrent.Future

class UserAllowListDeleteControllerSpec extends AnyFreeSpec with Matchers with ScalaFutures with MockitoSugar with BeforeAndAfterEach with OptionValues {

  override def beforeEach(): Unit = {
    super.beforeEach()
    Mockito.reset[Any](mockConnector, mockStubBehaviour)
  }

  private val mockConnector: UserAllowListConnector = mock[UserAllowListConnector]
  private val mockStubBehaviour: StubBehaviour = mock[StubBehaviour]

  private val frontendAuthComponents: FrontendAuthComponents =
    FrontendAuthComponentsStub(mockStubBehaviour)(stubControllerComponents(), global)

  private val app = GuiceApplicationBuilder()
    .overrides(
      bind[UserAllowListConnector].toInstance(mockConnector),
      bind[FrontendAuthComponents].toInstance(frontendAuthComponents)
    )
    .build()

  private implicit val messages: Messages = app.injector.instanceOf[MessagesApi].preferred(FakeRequest())

  private val permission = Predicate.Permission(
    resource = Resource(
      resourceType = ResourceType("user-allow-list-admin"),
      resourceLocation = ResourceLocation("service")
    ),
    action = IAAction("ADMIN")
  )

  private val form = app.injector.instanceOf[AllowListEntriesFormProvider].apply()

  "onPageLoad" - {

    "must display the page when the user is authorised" in {
      when(mockStubBehaviour.stubAuth[String](any(), any())).thenReturn(Future.successful("username"))
      val request = FakeRequest(GET, routes.UserAllowListDeleteController.onPageLoad("service").url)
        .withSession("authToken" -> "Token some-token")
      val result = route(app, request).value
      val view = app.injector.instanceOf[UserAllowListDeleteView]
      status(result) mustBe OK
      contentAsString(result) mustEqual view(form, "service")(request, implicitly).toString
      verify(mockStubBehaviour, times(1)).stubAuth(Some(permission), Retrieval.username)
    }

    "must fail when the user is not authenticated" in {
      val request = FakeRequest(GET, routes.UserAllowListDeleteController.onPageLoad("service").url) // no auth token
      val result = route(app, request).value
      status(result) mustBe SEE_OTHER
    }

    "must fail when the user is not authorised" in {
      when(mockStubBehaviour.stubAuth[String](any(), any())).thenReturn(Future.failed(new RuntimeException()))
      val request = FakeRequest(GET, routes.UserAllowListDeleteController.onPageLoad("service").url)
        .withSession("authToken" -> "Token some-token")
      route(app, request).value.failed.futureValue
    }
  }

  "onSubmit" - {

    "when a user is authenticated" - {

      "must update the allow list and redirect back to the service summary page when a user submits valid data" in {
        when(mockStubBehaviour.stubAuth[String](any(), any())).thenReturn(Future.successful("username"))
        when(mockConnector.delete(any(), any(), any())(any())).thenReturn(Future.successful(Done))
        val request = FakeRequest(POST, routes.UserAllowListDeleteController.onSubmit("service").url)
          .withSession("authToken" -> "Token some-token")
          .withFormUrlEncodedBody(
            "feature" -> "feature",
            "values" -> "a,b"
          )
        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustEqual routes.ServiceSummaryController.onPageLoad("service").url
        flash(result).get("user-allow-list-notification").value mustEqual messages("allow-list.delete.success.multiple", "feature")
        verify(mockStubBehaviour, times(1)).stubAuth(Some(permission), Retrieval.username)
        verify(mockConnector, times(1)).delete(eqTo("service"), eqTo("feature"), eqTo(Set("a", "b")))(any())
      }

      "must flash the value which has been deleted when only a single value is passed" in {
        when(mockStubBehaviour.stubAuth[String](any(), any())).thenReturn(Future.successful("username"))
        when(mockConnector.delete(any(), any(), any())(any())).thenReturn(Future.successful(Done))
        val request = FakeRequest(POST, routes.UserAllowListDeleteController.onSubmit("service").url)
          .withSession("authToken" -> "Token some-token")
          .withFormUrlEncodedBody(
            "feature" -> "feature",
            "values" -> "a"
          )
        val result = route(app, request).value
        status(result) mustBe SEE_OTHER
        redirectLocation(result).value mustEqual routes.ServiceSummaryController.onPageLoad("service").url
        flash(result).get("user-allow-list-notification").value mustEqual messages("allow-list.delete.success.single", "feature", "a")
      }

      "must return bad request and display the page with form errors when the user submits invalid data" in {
        when(mockStubBehaviour.stubAuth[String](any(), any())).thenReturn(Future.successful("username"))
        when(mockConnector.delete(any(), any(), any())(any())).thenReturn(Future.successful(Done))
        val request = FakeRequest(POST, routes.UserAllowListDeleteController.onSubmit("service").url)
          .withSession("authToken" -> "Token some-token")
          .withFormUrlEncodedBody()
        val result = route(app, request).value
        val view = app.injector.instanceOf[UserAllowListDeleteView]
        status(result) mustBe BAD_REQUEST
        contentAsString(result) mustEqual view(form.bind(Map.empty[String, String]), "service")(request, implicitly).toString
        verify(mockConnector, never).delete(any(), any(), any())(any())
      }

      "must fail when the user allow list connector fails" in {
        when(mockStubBehaviour.stubAuth[String](any(), any())).thenReturn(Future.successful("username"))
        when(mockConnector.delete(any(), any(), any())(any())).thenReturn(Future.failed(new RuntimeException()))
        val request = FakeRequest(POST, routes.UserAllowListDeleteController.onSubmit("service").url)
          .withSession("authToken" -> "Token some-token")
          .withFormUrlEncodedBody(
            "feature" -> "feature",
            "values" -> "a,b"
          )
        route(app, request).value.failed.futureValue
      }
    }

    "must fail when the user is not authenticated" in {
      val request = FakeRequest(POST, routes.UserAllowListDeleteController.onSubmit("service").url) // no auth token
      val result = route(app, request).value
      status(result) mustBe SEE_OTHER
    }

    "must fail when the user is not authorised" in {
      when(mockStubBehaviour.stubAuth[String](any(), any())).thenReturn(Future.failed(new RuntimeException()))
      val request = FakeRequest(POST, routes.UserAllowListDeleteController.onSubmit("service").url)
        .withSession("authToken" -> "Token some-token")
      route(app, request).value.failed.futureValue
    }
  }
}
