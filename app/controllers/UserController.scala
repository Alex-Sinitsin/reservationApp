package controllers

import com.mohiva.play.silhouette.api.Silhouette
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.services._
import play.api.libs.json.Json
import play.api.mvc._
import utils.auth.{HasSignUpMethod, JWTEnvironment}

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/**
 * Контроллер для работы с пользователями
 *
 * @param silhouette           Стек Silhouette
 * @param controllerComponents Экземпляр трейта `ControllerComponents`
 * @param credentialsProvider  Провайдер аутентификации по логину/паролю (Silhouette)
 * @param authInfoRepository   Репозиторий информации об авторизации (Silhouette)
 * @param hasSignUpMethod      Вспомогательная утилита для проверки наличия метода аутентификации
 * @param ex                   Контекст выполнения
 */
class UserController @Inject()(silhouette: Silhouette[JWTEnvironment],
                               controllerComponents: ControllerComponents,
                               userService: UserService,
                               credentialsProvider: CredentialsProvider,
                               authInfoRepository: AuthInfoRepository,
                               hasSignUpMethod: HasSignUpMethod)
                              (implicit ex: ExecutionContext) extends AbstractController(controllerComponents) {


  /**
   * Выводит список всех пользователей
   *
   * @return
   */
  def listAll(): Action[AnyContent] = silhouette.SecuredAction.async { implicit request: Request[AnyContent] =>
    userService.retrieveAll.flatMap { users => Future.successful(Ok(Json.toJson(users))) }
  }

  //TODO: Сделать метод обновления данных пользователя
  //TODO: Сделать метод смены роли пользователя

  /**
   * Обрабатывает удаление данных пользователя
   *
   * @return Результат выполнения операции
   */
  def delete(userID: UUID): Action[AnyContent] = silhouette.SecuredAction(hasSignUpMethod[JWTEnvironment#A](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[JWTEnvironment, AnyContent] =>

      request.identity.role match {
        case Some("Admin") =>
          userService.retrieveUserLoginInfo(userID, credentialsProvider.id).flatMap {
            case Some((user, loginInfo)) =>
              authInfoRepository.remove[PasswordInfo](loginInfo)
              userService.delete(userID, user.email).flatMap { delResult =>
                if (delResult) Future.successful(Ok(Json.obj("success" -> "Пользователь успешно удален!")))
                else Future.successful(BadRequest(Json.obj("error" -> "Произошла ошибка при удалении пользователя!")))
              }
            case None => Future.successful(BadRequest(Json.obj("error" -> "Пользователь не найден!")))
          }
        case _ => Future.successful(Forbidden(Json.obj("error" -> "Недостаточно прав для выполнения операции!")))
      }
  }
}
