package models.daos

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import models.{User, UserRoles}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile.api._

import java.time.ZonedDateTime
import scala.concurrent.{ExecutionContext, Future}

/**
 * Gives access to the user storage.
 */
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, userRolesDAO: UserRolesDAO)(implicit ec: ExecutionContext) extends UserDAO {

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  override def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]] = {
    //    db.run {
    //      users.filter(_.email === loginInfo.providerKey).result.headOption
    //    }
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- userLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- users.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser

    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.ID, user.name, user.lastName, user.position, user.email, Some(UserRoles.toHumanReadable(user.roleId)))
      }
    }
  }

  /**
   * Finds a user by its email
   *
   * @param email email of the user to find
   * @return The found user or None if no user for the given login info could be found
   */
  override def findByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(_.email === email).take(1).result.headOption).map(_ map DBUser.toUser)
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  override def save(user: User): Future[User] = {
//    db.run {
//      users returning users += user
//    }
      val actions = (for {
        userRoleId <- userRolesDAO.getUserRole
        dbUser = DBUser(user.ID, user.name, user.lastName, user.position, user.email, userRoleId)
        _ <- users.insertOrUpdate(dbUser)
      } yield ()).transactionally
      // run actions and return user afterwards
      db.run(actions).map(_ => user)
  }

  /**
   * Updates a user.
   *
   * @param user The user to update.
   * @return The saved user.
   */
//  override def update(user: User): Future[User] = db.run {
//    users.filter(_.email === user.email).update(user).map(_ => user)
//  }
}
