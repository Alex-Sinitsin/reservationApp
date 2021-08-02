package controllers

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import play.api.i18n.Lang
import play.api.libs.json.{JsString, _}
import play.api.mvc.{AnyContent, Request}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * The `Sign Up` controller.
 */
class SignUpController @Inject() (
  components: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(components) {

  implicit val userFormat = Json.format[User]

  /**
   * Handles sign up request
   *
   * @return The result to display.
   */
  def signUp = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    implicit val lang: Lang = supportedLangs.availables.head  // для вывода сообщений ошибок
    request.body.asJson.flatMap(_.asOpt[User]) match {
      case Some(newUser) if (newUser.password.nonEmpty) =>
        userService.retrieve(LoginInfo(CredentialsProvider.ID, newUser.email)).flatMap {
          case Some(_) =>
            Future.successful(Conflict(JsString(messagesApi("user.already.exist"))))
          case None =>
            val authInfo = passwordHasherRegistry.current.hash(newUser.password)
            val user = newUser.copy(password = authInfo.password)
            userService.save(user).map(u => Ok(Json.toJson(u.copy(password = null))))
        }
      case _ => Future.successful(BadRequest(JsString(messagesApi("invalid.body"))))
    }
  }
}
