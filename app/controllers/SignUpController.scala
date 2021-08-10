package controllers

import java.util.UUID
import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import forms.SignUpForm
import models.services._
import play.api.data.Form
import play.api.i18n.I18nSupport
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, Request}
import utils.auth.JWTEnvironment

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

/**
 * The `Sign Up` controller.
 *
 * @param components             The Play controller components.
 * @param silhouette             The Silhouette stack.
 * @param ex                     The execution context.
 */
class SignUpController @Inject()(components: ControllerComponents,
                                 silhouette: Silhouette[JWTEnvironment],
                                 signUpService: SignUpService,
                                )(implicit ex: ExecutionContext) extends AbstractController(components) with I18nSupport {

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    SignUpForm.form.bindFromRequest().fold(
      _ => Future.successful(Ok),
      data => {
        signUpService.signUpByCredentials(data).map {
          case UserCreated(user) =>
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
            Ok
          case UserAlreadyExists =>
            Conflict
        }
      }
    )
  }

  def signUp: Action[AnyContent] = TODO
}

//import com.mohiva.play.silhouette.api.LoginInfo
//import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
//import models.User
//import play.api.i18n.Lang
//import play.api.libs.json.{JsString, _}
//import play.api.mvc.{AnyContent, Request}
//
//import javax.inject.Inject
//import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign Up` controller.
 */
//class SignUpController @Inject() (
//  components: SilhouetteControllerComponents
//)(implicit ex: ExecutionContext) extends SilhouetteController(components) {
//
//  implicit val userFormat = Json.format[User]
//
//  /**
//   * Handles sign up request
//   *
//   * @return The result to display.
//   */
//  def signUp = UnsecuredAction.async { implicit request: Request[AnyContent] =>
//    implicit val lang: Lang = supportedLangs.availables.head
//    request.body.asJson.flatMap(_.asOpt[User]) match {
//      case Some(newUser) if newUser.password.isEmpty =>
//        userService.retrieve(LoginInfo(CredentialsProvider.ID, newUser.email)).flatMap {
//          case Some(_) =>
//            Future.successful(Conflict(JsString(messagesApi("user.already.exist"))))
//          case None =>
//            val authInfo = passwordHasherRegistry.current.hash(newUser.password)
//            val user = newUser.copy(password = authInfo.password)
//            userService.save(user).map(u => Ok(Json.toJson(u.copy(password = null))))
//        }
//      case _ => Future.successful(BadRequest(JsString(messagesApi("invalid.body"))))
//    }
//  }
//}
