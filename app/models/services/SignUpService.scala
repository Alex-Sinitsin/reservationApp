package models.services

import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.SignUpForm.CredentialsSignUpData

import javax.inject.Inject
import models.User

import scala.concurrent.{ExecutionContext, Future}

/**
 * Служба регистрации пользователей
 *
 * @param passwordHasherRegistry Реестр хэшей паролей
 * @param authenticateService    Служба аутентификации
 * @param userService            Реализация пользовательского сервиса
 * @param authTokenService       Реализация службы токенов аутентификации
 */
class SignUpService @Inject()(authTokenService: AuthTokenService,
                              authenticateService: AuthenticateService,
                              passwordHasherRegistry: PasswordHasherRegistry,
                              userService: UserService)(implicit ex: ExecutionContext) {

  /**
   * Регистрация пользователей по данным логина и пароля
   *
   * @param data Данные пользователя
   * @return
   */
  def signUpByCredentials(data: CredentialsSignUpData): Future[SignUpResult] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        Future.successful(UserAlreadyExists)
      case None =>
        val authInfo = passwordHasherRegistry.current.hash(data.password)
        for {
          user <- userService.createOrUpdate(loginInfo, data.email, data.name, data.lastName, data.position, None)
          _ <- authenticateService.addAuthenticateMethod(user.id, loginInfo, authInfo)
          authToken <- authTokenService.create(user.id)
        } yield UserCreated(user)
    }
  }
}

sealed trait SignUpResult
case object UserAlreadyExists extends SignUpResult
case class UserCreated(user: User) extends SignUpResult
