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

import play.api.i18n.I18nSupport
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.internalauth.client._
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.IndexView

import javax.inject.{Inject, Singleton}

@Singleton
class IndexController @Inject()(
                                 override val controllerComponents: MessagesControllerComponents,
                                 auth: FrontendAuthComponents,
                                 view: IndexView,
                               ) extends FrontendBaseController with I18nSupport {

  private val authenticated =
    auth.authenticatedAction(
      continueUrl = routes.IndexController.onPageLoad(),
      retrieval = Retrieval.locations(Some(ResourceType("user-allow-list-admin")))
    )

  def onPageLoad(): Action[AnyContent] =
    authenticated { implicit request =>
      Ok(view(request.retrieval))
    }
}
