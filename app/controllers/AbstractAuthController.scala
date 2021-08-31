package controllers

import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, Silhouette}
import models.User
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._
import utils.auth.JWTEnvironment

import scala.concurrent.{ExecutionContext, Future}

/**
 * `AbstractAuthController` контроллер с поддержкой аутентификации пользователя.
 *
 * @param silhouette    Стек Silhouette.
 * @param configuration Конфигурация Play.
 * @param clock         Экземпляр трейта `Clock`.
 * @param ex            Контекст выполнения.
 */
abstract class AbstractAuthController(silhouette: Silhouette[JWTEnvironment],
                                      configuration: Configuration,
                                      clock: Clock)(implicit ex: ExecutionContext) extends InjectedController with I18nSupport {

  case class UserWithToken(user: User, authToken: JWTEnvironment#A#Value)

  implicit val UserReads: OFormat[User] = Json.format[User]
  implicit val UserWithTokenFormat: OFormat[UserWithToken] = Json.format[UserWithToken]

  /**
   * Производит аутентификацию пользователя
   *
   * @param user       Пользовательские данные
   * @param loginInfo  Экземпляр класса `LoginInfo`
   * @param request    Первоначальный запрос
   * @return Результат выполнения аутентификации.
   */
  protected def authenticateUser(user: User, loginInfo: LoginInfo)(implicit request: Request[_]): Future[AuthenticatorResult] = {
    silhouette.env.authenticatorService.create(loginInfo).map(authenticator => authenticator).flatMap { authenticator =>
      silhouette.env.eventBus.publish(LoginEvent(user, request))
      silhouette.env.authenticatorService.init(authenticator).flatMap { token =>
        silhouette.env.authenticatorService.embed(token, Ok(Json.toJson(Json.obj("status" -> "success", "data" -> UserWithToken(user, token)))))
      }
    }
  }
}
