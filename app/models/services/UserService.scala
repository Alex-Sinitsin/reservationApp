package models.services

//import com.mohiva.play.silhouette.api.services.IdentityService
//import models.User
//
//import scala.concurrent.Future
//
///**
// * Handles actions to users.
// */
//trait UserService extends IdentityService[User] {
//
//  /**
//   * Saves a user.
//   *
//   * @param user The user to save.
//   * @return The saved user.
//   */
//  def save(user: User): Future[User]
//
//  /**
//   * Updates a user.
//   *
//   * @param user The user to update.
//   * @return The updated user.
//   */
//  def update(user: User): Future[User]
//}

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.{User, UserRoles}

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
trait UserService extends IdentityService[User] {
  /**
   * Changes role of user
   *
   * @param userId user id
   * @param role   role to assign to user
   * @return
   */
  def changeUserRole(userId: UUID, role: String): Future[Boolean]

  /**
   * Retrieves a user and login info pair by userID and login info providerID
   *
   * @param id         The ID to retrieve a user.
   * @param providerID The ID of login info provider.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieveUserLoginInfo(id: UUID, providerID: String): Future[Option[(User, LoginInfo)]]

  /**
   * Creates or updates a user
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
  def createOrUpdate(loginInfo: LoginInfo, email: String, name: String, lastName: String, position: String): Future[User]
}