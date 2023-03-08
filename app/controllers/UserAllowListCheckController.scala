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
import forms.AllowListEntryFormProvider
import models.{AllowListEntries, AllowListEntry}
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.internalauth.client.Predicate.Permission
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.UserAllowListCheckView

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserAllowListCheckController @Inject()(
                                             override val controllerComponents: MessagesControllerComponents,
                                             auth: FrontendAuthComponents,
                                             view: UserAllowListCheckView,
                                             formProvider: AllowListEntryFormProvider,
                                             connector: UserAllowListConnector
                                           )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form: Form[AllowListEntry] = formProvider()

  private def permission(service: String) = Permission(
    Resource(
      ResourceType("user-allow-list-admin"),
      ResourceLocation(service),
    ),
    IAAction("ADMIN")
  )

  private def authorised(service: String) =
    auth.authorizedAction(
      continueUrl = routes.UserAllowListCheckController.onPageLoad(service),
      predicate = permission(service),
      retrieval = Retrieval.username
    )

  def onPageLoad(service: String): Action[AnyContent] =
    authorised(service) { implicit request =>
      Ok(view(form, service))
    }

  def onSubmit(service: String): Action[AnyContent] =
    authorised(service).async { implicit request =>
      form.bindFromRequest().fold(
        formWithErrors =>
          Future.successful(BadRequest(view(formWithErrors, service))),
        entry =>
          connector.check(service, entry.feature, entry.value).map { exists =>
            Redirect(routes.UserAllowListCheckController.onPageLoad(service))
              .flashing("user-allow-list" -> exists.toString)
          }
      )
    }
}
