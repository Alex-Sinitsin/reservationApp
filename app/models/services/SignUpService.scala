package models.services

import java.util.UUID
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasherRegistry
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import javax.inject.Inject
import models.User
import play.api.libs.json.{Json, OFormat}

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
          _ <- authenticateService.addAuthenticateMethod(user.ID, loginInfo, authInfo)
          authToken <- authTokenService.create(user.ID)
        } yield UserCreated(user)
    }
  }
}

sealed trait SignUpResult
case object UserAlreadyExists extends SignUpResult
case class UserCreated(user: User) extends SignUpResult

/**
 * CredentialsSingUpData
 *
 * @param name       The first name of a user.
 * @param lastName        The last name of a user.
 * @param position        The last name of a user.
 * @param email           The email of the user.
 * @param password        The password of the user.
 */
case class CredentialsSingUpData(name: String, lastName: String, position: String, email: String, password: String)
object CredentialsSingUpData {
  implicit val credentialsSingUpDataFormat: OFormat[CredentialsSingUpData] = Json.format[CredentialsSingUpData]
}
