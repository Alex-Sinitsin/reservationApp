package controllers

import com.mohiva.play.silhouette.api.{LoginEvent, LoginInfo, Silhouette}
import com.mohiva.play.silhouette.api.services.AuthenticatorResult
import com.mohiva.play.silhouette.api.util.Clock

import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{Json, OFormat}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

import models.User
import utils.auth.JWTEnvironment

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

  implicit val UserReads: OFormat[User] = Json.format[User]

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
        silhouette.env.authenticatorService.embed(token, Ok(Json.toJson(user)))
      }
    }
  }
}
