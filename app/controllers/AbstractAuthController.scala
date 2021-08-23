package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import com.mohiva.play.silhouette.api.util.Clock
import models.{User, UserRoles}
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import utils.auth.JWTEnvironment

import scala.concurrent.{ExecutionContext, Future}

/**
 * `AbstractAuthController` base with support methods to authenticate an user.
 *
 * @param silhouette    The Silhouette stack.
 * @param configuration The Play configuration.
 * @param clock         The clock instance.
 * @param ex            The execution context.
 */
abstract class AbstractAuthController(silhouette: Silhouette[JWTEnvironment],
                                      configuration: Configuration,
                                      clock: Clock)(implicit ex: ExecutionContext) extends InjectedController with I18nSupport {

  implicit val UserReads: OFormat[User] = Json.format[User]

  /**
   * Performs user authentication
   *
   * @param user       User data
   * @param request    Initial request
   * @return The result to display.
   */
  protected def authenticateUser(user: User, loginInfo: LoginInfo)(implicit request: Request[_]): Future[AuthenticatorResult] = {
    silhouette.env.authenticatorService.create(loginInfo).map(authenticator => authenticator).flatMap { authenticator =>
      silhouette.env.eventBus.publish(LoginEvent(user, request))
      silhouette.env.authenticatorService.init(authenticator).flatMap { token =>
        silhouette.env.authenticatorService.embed(token, Ok(Json.toJson(user)))
      }
    }
  }
}
