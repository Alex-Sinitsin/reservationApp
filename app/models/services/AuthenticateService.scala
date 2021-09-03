package models.services

import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import javax.inject.Inject

import scala.concurrent.{ExecutionContext, Future}

import models.User
import models.daos.LoginInfoDAO
import java.util.UUID

/**
 * Служба аутентификации
 *
 * @param userService             Сервис управления пользователями.
 * @param credentialsProvider     Поставщик учетных данных.
 * @param authInfoRepository      Репозиторий информации об авторизации.
 * @param ec                      Контекст выполнения
 */
class AuthenticateService @Inject()(credentialsProvider: CredentialsProvider,
                                    userService: UserService,
                                    authInfoRepository: AuthInfoRepository,
                                    loginInfoDAO: LoginInfoDAO)(implicit ec: ExecutionContext) {

  /**
   * Учетные данные
   *
   * @param email Электронная почта
   * @param password Пароль
   * @return
   */
  def credentials(email: String, password: String): Future[AuthenticateResult] = {
    val credentials = Credentials(email, password)
    credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
      userService.retrieve(loginInfo).map {
        case Some(user) =>
          Success(user)
        case None =>
          UserNotFound
      }
    }.recoverWith {
      case _: InvalidPasswordException =>
        Future.successful(InvalidPassword("Неправильный логин или пароль!"))
      case _: IdentityNotFoundException =>
        Future.successful(UserNotFound)
      case e =>
        Future.failed(e)
    }
  }

  /**
   * Добавляет метод аутентификации для пользователя
   *
   * @param userId    ID пользователя
   * @param loginInfo Данные для входа
   * @param authInfo  Данные об авторизации
   * @tparam T тип данных информации об авторизации
   * @return
   */
  def addAuthenticateMethod[T <: AuthInfo](userId: UUID, loginInfo: LoginInfo, authInfo: T): Future[Unit] = {
    for {
      _ <- loginInfoDAO.saveUserLoginInfo(userId, loginInfo)
      _ <- authInfoRepository.add(loginInfo, authInfo)
    } yield ()
  }

  /**
   * Проверяет наличие метода аутентификации у пользователя для выбранного провайдера
   *
   * @param userId     ID пользователя
   * @param providerId ID провайдера аутентификации
   * @return true, если есть метод аутентификации, иначе false
   */
  def userHasAuthenticationMethod(userId: UUID, providerId: String): Future[Boolean] = {
    loginInfoDAO.find(userId, providerId).map(_.nonEmpty)
  }

  /**
   * Получение списка провайдеров и методов аутентификации пользователей
   *
   * @param email Электронная почта пользователя
   * @return
   */
  def getAuthenticationProviders(email: String): Future[Seq[String]] = loginInfoDAO.getAuthenticationProviders(email)
}

sealed trait AuthenticateResult
case class Success(user: User) extends AuthenticateResult
case class InvalidPassword(msg: String) extends AuthenticateResult
object UserNotFound extends AuthenticateResult