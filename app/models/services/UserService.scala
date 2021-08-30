package models.services

import java.util.UUID
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import models.User

import scala.concurrent.Future

/**
 * Обрабатывает действия для пользователей
 */
trait UserService extends IdentityService[User] {


  /**
   * Извлекает список пользователей
   *
   * @return
   */
  def retrieveAll: Future[Seq[User]]

  /**
   * Меняет роль пользователя
   *
   * @param userId ID пользователя
   * @param role   Новая роль, которую необходимо присвоить пользователю
   * @return
   */
  def changeUserRole(userId: UUID, role: String): Future[Boolean]

  /**
   * Извлекает пару информации о пользователе и информации для входа по идентификатору пользователя и идентификатору поставщика информации для входа
   *
   * @param id         ID пользователя
   * @param providerID ID провайдера
   * @return Полученный пользователь или None, если не удалось получить пользователя для данного идентификатора
   */
  def retrieveUserLoginInfo(id: UUID, providerID: String): Future[Option[(User, LoginInfo)]]

  /**
   * Создает или обновляет информацию о пользователе
   *
   * Если пользователь существует для данной информации для входа или электронной почты, обновляет пользователя, в противном случае создает нового пользователя с заданными данными
   *
   * @param loginInfo Данные для входа
   * @param email     Электронная почта пользователя
   * @param name      Имя пользователя
   * @param lastName  Фамилия пользователя
   * @param position  Должность пользователя в компании
   * @return
   */
  def createOrUpdate(loginInfo: LoginInfo, email: String, name: String, lastName: String, position: String): Future[User]

  /**
   * Удаляет данные пользователя
   *
   * @param userID ID пользователя
   * @param email Email пользователя
   * @return
   */
  def delete(userID: UUID, email: String): Future[Boolean]
}