package controllers

import com.mohiva.play.silhouette.api._
import forms.SignUpForm
import models.services._
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc._
import utils.auth.JWTEnvironment

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Контроллер регистрации пользователей
 *
 * @param ControllerComponents Экземпляр трейта `ControllerComponents`
 * @param silhouette Стек Silhouette
 * @param signUpService Служба регистрации пользователей
 * @param ex Контекст выполнения
 */
class SignUpController @Inject()(ControllerComponents: MessagesControllerComponents,
                                 silhouette: Silhouette[JWTEnvironment],
                                 signUpService: SignUpService,
                                )(implicit ex: ExecutionContext) extends MessagesAbstractController(ControllerComponents) with I18nSupport {
  /**
   * Обработка данных отправленной формы регистрации.
   *
   * @return Результат выполнения.
   */
  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>

    SignUpForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(Json.toJson(formWithErrors.errors.toString))),
      data => {
        if(data.password == data.confirmPassword) {
          signUpService.signUpByCredentials(data).map {
            case UserCreated(user) =>
              silhouette.env.eventBus.publish(SignUpEvent(user, request))
              Ok(Json.obj("result" -> "Пользователь успешно добавлен!"))
            case UserAlreadyExists =>
              Conflict(Json.obj("error" -> "Пользователь уже существует!"))
          }
        } else {
          Future.successful(BadRequest(Json.obj("errors" -> "Введенные пароли не совпадают!")))
        }
      }
    )
  }
}