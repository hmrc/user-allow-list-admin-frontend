package controllers

import play.api.mvc.MessagesControllerComponents
import uk.gov.hmrc.internalauth.client.FrontendAuthComponents
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import javax.inject.{Inject, Singleton}

@Singleton
class UserAllowListSetController @Inject() (
                                             override val controllerComponents: MessagesControllerComponents,
                                             auth: FrontendAuthComponents
                                           ) extends FrontendBaseController {


}
