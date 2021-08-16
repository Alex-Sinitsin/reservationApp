package models.daos

import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Future
import models.User

import java.util.UUID

/**
 * Предоставляет доступ к хранилищу пользователей.
 */
trait UserDAO extends DatabaseDAO {
  //TODO: Добавить метод для удаления информации о пользователе
  //TODO: Добавить метод для смены роли у пользователя

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
   * Сохраняет или обновляет информацию о пользователе
   *
   * @param user Информация о пользователе, которого необходимо сохранить
   * @return Сохраненный пользователь
   */
  def save(user: User): Future[User]

//  /**
//   * Удаляет информацию о пользователе
//   *
//   * @param userID ID пользователя
//   * @return
//   */
//  def remove(userID: Int): Future[Unit]
}
