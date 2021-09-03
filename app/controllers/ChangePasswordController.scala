package controllers

import javax.inject.Inject
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Credentials, PasswordHasherRegistry, PasswordInfo}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ChangePasswordForm
import play.api.i18n.{I18nSupport, Messages}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import utils.auth.{HasSignUpMethod, JWTEnvironment}

import scala.concurrent.{ExecutionContext, Future}

class ChangePasswordController @Inject()(components: ControllerComponents,
                                         silhouette: Silhouette[JWTEnvironment],
                                         credentialsProvider: CredentialsProvider,
                                         authInfoRepository: AuthInfoRepository,
                                         hasSignUpMethod: HasSignUpMethod,
                                         passwordHasherRegistry: PasswordHasherRegistry)(implicit ex: ExecutionContext) extends AbstractController(components) with I18nSupport {
  /**
   * Изменяет пароль пользователя
   *
   * @return Результат выполнения
   */
  def submit: Action[AnyContent] = silhouette.SecuredAction(hasSignUpMethod[JWTEnvironment#A](CredentialsProvider.ID)).async { implicit request: SecuredRequest[JWTEnvironment, AnyContent] =>
      ChangePasswordForm.form.bindFromRequest().fold(
        formWithErrors => Future.successful(BadRequest(formWithErrors.errors.toString)),
        data => {
          val (currentPassword, newPassword, _) = data
          val credentials = Credentials(request.identity.email, currentPassword)
          credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
            val passwordInfo = passwordHasherRegistry.current.hash(newPassword)
            authInfoRepository.update[PasswordInfo](loginInfo, passwordInfo).map { _ =>
              Ok(Json.obj("success" -> Messages("success.password.changed")))
            }
          }.recover {
            case _: ProviderException =>
              BadRequest(Json.obj("error" -> Messages("current.password.invalid")))
          }
        }
      )
  }
}
