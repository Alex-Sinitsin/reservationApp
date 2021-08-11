package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import forms.SignUpForm
import models.services._
import play.api.i18n.I18nSupport
import play.api.libs.json._
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents, MessagesAbstractController, MessagesControllerComponents, Request}
import utils.auth.JWTEnvironment

import scala.concurrent.{ExecutionContext, Future}

/**
 * Контроллер `Sign Up`.
 *
 * @param components             Компоненты Play контроллера.
 * @param silhouette             Компоненты Silhouette.
 * @param ex                     Контекст выполнения.
 */
class SignUpController @Inject()(components: MessagesControllerComponents,
                                 silhouette: Silhouette[JWTEnvironment],
                                 signUpService: SignUpService,
                                )(implicit ex: ExecutionContext) extends MessagesAbstractController(components) with I18nSupport {
  /**
   * Обработка данных формы регистрации.
   *
   * @return Результат выполнения.
   */
  def submit: Action[AnyContent] = silhouette.UnsecuredAction.async { implicit request: Request[AnyContent] =>
    //TODO: Сделать возврат формы с ошибками в результат
    SignUpForm.form.bindFromRequest().fold(
      formWithErrors => Future.successful(BadRequest(formWithErrors.errors.toString())),
      data => {
        signUpService.signUpByCredentials(data).map {
          case UserCreated(user) =>
            silhouette.env.eventBus.publish(SignUpEvent(user, request))
            Ok(Json.obj("result" -> "Пользователь успешно добавлен"))
          case UserAlreadyExists =>
            Conflict(Json.obj("error" -> "Пользователь уже существует"))
        }
      }
    )
  }

  def signUp: Action[AnyContent] = silhouette.UnsecuredAction { implicit request: Request[AnyContent] =>
    Ok(views.html.register(SignUpForm.form))
  }
}