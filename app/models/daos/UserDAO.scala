package models.daos

import com.mohiva.play.silhouette.api.LoginInfo
import models.User

import scala.concurrent.Future

/**
 * Gives access to the user storage.
 */
trait UserDAO extends DatabaseDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]]

  /**
   * Finds a user by its email
   *
   * @param email email of the user to find
   * @return The found user or None if no user for the given login info could be found
   */
  def findByEmail(email: String): Future[Option[User]]

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User]

  /**
   * Updates a user.
   *
   * @param user The user to update.
   * @return The saved user.
   */
//  def update(user: User): Future[User]
}
