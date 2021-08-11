package models.services

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.User
import models.daos.{LoginInfoDAO, UserDAO}

import scala.concurrent.{ExecutionContext, Future}

/**
 * Handles actions to users.
 *
 * @param userDAO The user DAO implementation.
 * @param ec Execution Context
 */
class UserServiceImpl @Inject()(userDAO: UserDAO,
                                loginInfoDAO: LoginInfoDAO)(implicit ec: ExecutionContext) extends UserService {

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param loginInfo The login info to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given login info.
   */
  def retrieve(loginInfo: LoginInfo): Future[Option[User]] = userDAO.findByLoginInfo(loginInfo)

  /**
   * Retrieves a user and login info pair by userID and login info providerID
   *
   * @param id         The ID to retrieve a user.
   * @param providerID The ID of login info provider.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieveUserLoginInfo(id: UUID, providerID: String): Future[Option[(User, LoginInfo)]] = {
    loginInfoDAO.find(id, providerID)
  }

  /**
   * Changes role of user
   *
   * @param userId user id
   * @param role   role to assign to user
   * @return
   */
  override def changeUserRole(userId: UUID, role: String): Future[Boolean] = ???
//  override def changeUserRole(userId: UUID, role: UserRoles.Value): Future[Boolean] = {
//    userDAO.updateUserRole(userId, role)
//  }

  /**
   * Creates or updates user
   *
   * If a user exists for given login info or email then update the user, otherwise create a new user with the given data
   *
   * @param loginInfo social profile
   * @param email     user email
   * @param name first name
   * @param lastName  last name
   * @param position company position
   * @return
   */
  override def createOrUpdate(loginInfo: LoginInfo, email: String, name: String, lastName: String, position: String): Future[User] = {

    Future.sequence(Seq(userDAO.findByLoginInfo(loginInfo),
      userDAO.findByEmail(email))).flatMap { users =>
      users.flatten.headOption match {
        case Some(user) =>
          userDAO.save(user.copy(
            name = name,
            lastName = lastName,
            email = email,
            position = position
          ))
        case None =>
          userDAO.save(User(
            id = UUID.randomUUID(),
            name = name,
            lastName = lastName,
            email = email,
            position = position,
            role = None
          ))
      }
    }
  }
}
