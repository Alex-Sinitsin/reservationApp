package models.daos

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.{ExecutionContext, Future}
import models.{User, UserRoles}

import java.util.UUID

/**
 * Реализация объекта для доступа к хранилищу пользователей
 *
 * @param dbConfigProvider Провайдер конфигурации базы данных
 * @param userRolesDAO Объект для доступа к хранилищу ролей пользователя
 * @param ec Контекст выполнения
 */
class UserDAOImpl @Inject() (protected val dbConfigProvider: DatabaseConfigProvider, userRolesDAO: UserRolesDAO)(implicit ec: ExecutionContext) extends UserDAO {

  /**
   * Находит информацию о пользователе по данным для входа
   *
   * @param loginInfo Информация для входа пользователя, которого необходимо найти
   * @return Найденный пользователь или None, если пользователь не найден
   */
  override def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]] = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- userLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- users.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser

    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        User(user.id, user.name, user.lastName, user.position, user.email, Some(UserRoles.toHumanReadable(user.roleId)))
      }
    }
  }

  /**
   * Находит информацию о пользователе по его Email
   *
   * @param email Электронная почта пользователя, которого необходимо найти
   * @return Найденный пользователь или None, если пользователь не найден
   */
  override def findByEmail(email: String): Future[Option[User]] = {
    db.run(users.filter(_.email === email).take(1).result.headOption).map(_ map DBUser.toUser)
  }

  /**
   * Находит информацию о пользователе по его ID
   *
   * @param userID ID пользователя, которого необходимо найти
   * @return Найденный пользователь или None, если пользователь не найден
   */
  override def findByID(userID: UUID): Future[Option[User]] = {
    db.run(users.filter(_.id === userID).take(1).result.headOption).map(_ map DBUser.toUser)
  }

  /**
   * Находит пользователей по их ID
   *
   * @param userIDs ID пользователей, которых необходимо найти
   * @return Найденные пользователи
   */
  override def findUsersByID(userIDs: Seq[UUID]): Future[Seq[User]] =
    db.run(users.filter(_.id inSetBind userIDs).result).map(_.map {usrs => DBUser.toUser(usrs)})

  /**
   * Сохраняет информацию о пользователе
   *
   * @param user Информация о пользователе, которого необходимо сохранить
   * @return Сохраненный пользователь
   */
  override def save(user: User): Future[User] = {
    val actions = (for {
      userRoleId <- userRolesDAO.getUserRole
      dbUser = DBUser(user.id, user.name, user.lastName, user.position, user.email, userRoleId)
      _ <- users.insertOrUpdate(dbUser)
    } yield ()).transactionally

    db.run(actions).map(_ => user)
  }

  /**
   * Меняет роль пользователя
   *
   * @param userId ID пользователя
   * @param role   Новая роль, которую необходимо присвоить пользователю
   *  @return
   */
  override def updateUserRole(userId: UUID, role: String): Future[Boolean] = {
    db.run(users.filter(_.id === userId).map(_.roleId).update(UserRoles.toDBReadable(role))).map(_ > 0)
  }

  /**
   * Удаляет пользовательские данные
   *
   * @param userID ID пользователя
   *  @return
   */
  override def delete(userID: UUID): Future[Boolean] =
    db.run(users.filter(_.id === userID).delete.map(_ > 0))
}
