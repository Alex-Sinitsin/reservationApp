package models.daos

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import models.{DatabaseSchema, User}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}

/**
 * Gives access to the user storage.
 */
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends UserDAO with HasDatabaseConfigProvider[JdbcProfile] with DatabaseSchema {


  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  override def find(loginInfo: LoginInfo): Future[Option[User]] = db.run {
    users.filter(_.email === loginInfo.providerKey).result.headOption
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = db.run {
    users returning users += user
  }

  /**
   * Updates a user.
   *
   * @param user The user to update.
   * @return The saved user.
   */
  override def update(user: User): Future[User] = db.run {
    users.filter(_.email === user.email).update(user).map(_ => user)
  }
}
