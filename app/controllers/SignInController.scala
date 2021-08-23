package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.SignInForm

import javax.inject.Inject
import models.services._
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Request}
import utils.auth.JWTEnvironment

import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign In` controller.
 *
 * @param silhouette             The Silhouette stack.
 * @param configuration          The Play configuration.
 * @param clock                  The clock instance.
 */
class SignInController @Inject()(authenticateService: AuthenticateService,
                                 silhouette: Silhouette[JWTEnvironment],
                                 configuration: Configuration,
                                 clock: Clock)(implicit ex: ExecutionContext)
  extends AbstractAuthController(silhouette, configuration, clock) with I18nSupport with Logger {

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    //TODO: Сделать возврат формы с ошибками в результат
    SignInForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errors.toString())),
      data => {
        authenticateService.credentials(data.email, data.password).flatMap {
          case Success(user) =>
            val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            authenticateUser(user, loginInfo)
          case InvalidPassword(msg) =>
            Future.successful(Forbidden(Json.obj("errors" -> msg)))
          case UserNotFound => Future.successful(Forbidden(Json.obj("errors" -> "Пользователь не найден")))
          }
          .recover {
            case e =>
              logger.error(s"Sign in error email = ${data.email}", e)
              InternalServerError(Json.obj("errorCode" -> "SystemError"))
          }
      }
    )
  }
}

//import com.mohiva.play.silhouette.api.exceptions.ProviderException
//import com.mohiva.play.silhouette.api.util.Credentials
//import play.api.i18n.Lang
//import play.api.libs.json.{JsString, Json}
//import play.api.mvc.{AnyContent, Request}
//
//import javax.inject.Inject
//import scala.concurrent.{ExecutionContext, Future}
//
///**
// * The `Sign In` controller.
// */
//class SignInController @Inject() (
//  scc: SilhouetteControllerComponents
//)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {
//
//  case class SignInModel(email: String, password: String)
//
//  implicit val signInFormat = Json.format[SignInModel]
//
//  /**
//   * Handles sign in request
//   *
//   * @return JWT token in header if login is successful or Bad request if credentials are invalid
//   */
//  def signIn = UnsecuredAction.async { implicit request: Request[AnyContent] =>
//    implicit val lang: Lang = supportedLangs.availables.head
//    request.body.asJson.flatMap(_.asOpt[SignInModel]) match {
//      case Some(signInModel) =>
//        val credentials = Credentials(signInModel.email, signInModel.password)
//        credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
//          userService.retrieve(loginInfo).flatMap {
//            case Some(_) =>
//              for {
//                authenticator <- authenticatorService.create(loginInfo)
//                token <- authenticatorService.init(authenticator)
//                result <- authenticatorService.embed(token, Ok)
//              } yield {
//                logger.debug(s"User ${loginInfo.providerKey} signed success")
//                result
//              }
//            case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
//          }
//        }.recover {
//          case _: ProviderException => BadRequest(JsString(messagesApi("invalid.credentials")))
//        }
//      case None => Future.successful(BadRequest(JsString(messagesApi("could.not.find.user"))))
//    }
//  }
//}
