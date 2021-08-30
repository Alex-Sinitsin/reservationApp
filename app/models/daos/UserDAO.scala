package models.daos

import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Future
import models.User

import java.util.UUID

/**
 * Предоставляет доступ к хранилищу пользователей.
 */
trait UserDAO extends DatabaseDAO {

  /**
   * Меняет роль пользователя
   *
   * @param userId ID пользователя
   * @param role   Новая роль, которую необходимо присвоить пользователю
   * @return
   */
  def updateUserRole(userId: UUID, role: String): Future[Boolean]

  /**
   * Находит пользователя по информации для входа
   *
   * @param loginInfo Информация для входа пользователя, которого необходимо найти
   * @return Найденный пользователь или None, если пользователь не найден
   */
  def findByLoginInfo(loginInfo: LoginInfo): Future[Option[User]]

  /**
   * Находит пользователя по его Email
   *
   * @param email Электронная почта пользователя, которого необходимо найти
   * @return Найденный пользователь или None, если пользователь не найден
   */
  def findByEmail(email: String): Future[Option[User]]

  /**
   * Находит пользователя по его ID
   *
   * @param userID ID пользователя, которого необходимо найти
   * @return Найденный пользователь или None, если пользователь не найден
   */
  def findByID(userID: UUID): Future[Option[User]]

  /**
   * Находит пользователей по их ID
   *
   * @param userIDs ID пользователей, которых необходимо найти
   * @return Найденные пользователи
   */
  def findUsersByID(userIDs: Seq[UUID]): Future[Seq[User]]

  /**
   * Извлекает список всех пользователей
   *
   * @return
   */
  def getAll: Future[Seq[User]]

  /**
   * Сохраняет или обновляет информацию о пользователе
   *
   * @param user Информация о пользователе, которого необходимо сохранить
   * @return Сохраненный пользователь
   */
  def save(user: User): Future[User]

  /**
   * Удаляет информацию о пользователе
   *
   * @param userID ID пользователя
   * @return
   */
  def delete(userID: UUID): Future[Boolean]
}
