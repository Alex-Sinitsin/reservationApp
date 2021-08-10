package models.services

import com.mohiva.play.silhouette.api.{AuthInfo, LoginInfo}
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.Credentials
import com.mohiva.play.silhouette.impl.exceptions.{IdentityNotFoundException, InvalidPasswordException}
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import models.User
import models.daos.LoginInfoDAO

import java.util.UUID
import javax.inject.{Inject, Named}
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.DurationInt

/**
 *
 * @param userService             The user service implementation.
 * @param credentialsProvider     The credentials provider.
 * @param authInfoRepository      The auth info repository implementation.
 * @param ec                      The execution context.
 */
class AuthenticateService @Inject()(credentialsProvider: CredentialsProvider,
                                    userService: UserService,
                                    authInfoRepository: AuthInfoRepository,
                                    loginInfoDAO: LoginInfoDAO)(implicit ec: ExecutionContext) {

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
        Future.successful(InvalidPassword("Неправильный логин или пароль"))
      case _: IdentityNotFoundException =>
        Future.successful(UserNotFound)
      case e =>
        Future.failed(e)
    }
  }

  /**
   * Adds authentication method to user
   *
   * @param userId    user id
   * @param loginInfo login info
   * @param authInfo  auth info
   * @tparam T type of auth info
   * @return
   */
  def addAuthenticateMethod[T <: AuthInfo](userId: UUID, loginInfo: LoginInfo, authInfo: T): Future[Unit] = {
    for {
      _ <- loginInfoDAO.saveUserLoginInfo(userId, loginInfo)
      _ <- authInfoRepository.add(loginInfo, authInfo)
    } yield ()
  }

  /**
   * Checks whether user have authentication method for given provider id
   *
   * @param userId     user id
   * @param providerId authentication provider id
   * @return true if user has authentication method for given provider id, otherwise false
   */
  def userHasAuthenticationMethod(userId: UUID, providerId: String): Future[Boolean] = {
    loginInfoDAO.find(userId, providerId).map(_.nonEmpty)
  }

  /**
   * Get list of providers of user authentication methods
   *
   * @param email user email
   * @return
   */
  def getAuthenticationProviders(email: String): Future[Seq[String]] = loginInfoDAO.getAuthenticationProviders(email)
}

sealed trait AuthenticateResult
case class Success(user: User) extends AuthenticateResult
case class InvalidPassword(msg: String) extends AuthenticateResult
object UserNotFound extends AuthenticateResult