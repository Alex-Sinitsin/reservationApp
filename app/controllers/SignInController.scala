package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.util.Clock
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import javax.inject.Inject
import play.api.Configuration
import play.api.i18n.I18nSupport
import play.api.libs.json.{JsString, Json}
import play.api.mvc.{Action, AnyContent, Request}
import forms.SignInForm
import models.daos.UserDAO
import models.services._
import utils.auth.JWTEnvironment

import scala.concurrent.{ExecutionContext, Future}

/**
 * Контроллер авторизации пользователей
 *
 * @param authenticateService Служба аутентификации пользователей
 * @param silhouette    Стек Silhouette
 * @param configuration Конфигурация Play.
 * @param clock         Экземпляр трейта `Clock`
 * @param ex            Контекст выполнения
 */
class SignInController @Inject()(authenticateService: AuthenticateService,
                                 silhouette: Silhouette[JWTEnvironment],
                                 configuration: Configuration,
                                 userDAO: UserDAO,
                                 clock: Clock)(implicit ex: ExecutionContext)
  extends AbstractAuthController(silhouette, configuration, clock) with I18nSupport with Logger {

  /**
   * Обработка данных отправленной формы авторизации.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>

    SignInForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString()))),
      data => {
        authenticateService.credentials(data.email, data.password).flatMap {
          case Success(user) =>
            val loginInfo = LoginInfo(CredentialsProvider.ID, user.email)
            authenticateUser(user, loginInfo)
          case InvalidPassword(msg) =>
            Future.successful(Forbidden(Json.obj("errors" -> msg)))
          case UserNotFound => Future.successful(Forbidden(Json.obj("errors" -> "Пользователь не найден!")))
          }
          .recover {
            case e =>
              logger.error(s"Ошибка авторизации по email = ${data.email}", e)
              InternalServerError(Json.obj("errorCode" -> "SystemError"))
          }
      }
    )
  }

  /**
   * Обрабатывает выход из системы
   *
   * @return Результат выполнения
   */
  def signOut(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: SecuredRequest[JWTEnvironment, AnyContent] =>
    silhouette.env.eventBus.publish(LogoutEvent(request.identity, request))
    silhouette.env.authenticatorService.discard(request.authenticator, Ok)
  }
}