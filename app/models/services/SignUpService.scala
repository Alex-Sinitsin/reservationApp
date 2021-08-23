package models.services

import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.CredentialsSingUpData

import javax.inject.Inject
import models.User

import scala.concurrent.{ExecutionContext, Future}

/**
 *
 * @param passwordHasherRegistry The password hasher registry.
 * @param authenticateService    The authenticate service
 * @param userService            The user service implementation.
 * @param authTokenService       The auth token service implementation.
 */
class SignUpService @Inject()(authTokenService: AuthTokenService,
                              authenticateService: AuthenticateService,
                              passwordHasherRegistry: PasswordHasherRegistry,
                              userService: UserService)(implicit ex: ExecutionContext) {

  def signUpByCredentials(data: CredentialsSingUpData): Future[SignUpResult] = {
    val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        Future.successful(UserAlreadyExists)
      case None =>
        val authInfo = passwordHasherRegistry.current.hash(data.password)
        for {
          user <- userService.createOrUpdate(loginInfo, data.email, data.name, data.lastName, data.position)
          _ <- authenticateService.addAuthenticateMethod(user.id, loginInfo, authInfo)
          authToken <- authTokenService.create(user.id)
        } yield UserCreated(user)
    }
  }
}

sealed trait SignUpResult
case object UserAlreadyExists extends SignUpResult
case class UserCreated(user: User) extends SignUpResult
