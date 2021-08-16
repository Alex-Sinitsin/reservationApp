package models.daos

import com.mohiva.play.silhouette.api.LoginInfo

import scala.concurrent.Future

import models.User

import java.util.UUID

trait LoginInfoDAO {
  /**
   * Получает список провайдеров аутентификации пользователей
   *
   * @param email user email
   * @return
   */
  def getAuthenticationProviders(email: String): Future[Seq[String]]

  /**
   * Находит пользователя и информацию авторизации по ID пользователя и ID провайдера аутентификации
   *
   * @param userId     ID пользователя
   * @param providerId ID провайдера аутентификации
   * @return Some(User, LoginInfo), если пользователь с данным ID имеет метод аутентификации для данного провайдера по ID провайдера, иначе None
   */
  def find(userId: UUID, providerId: String): Future[Option[(User, LoginInfo)]]

  /**
   * Сохраняет данные входа для пользователя
   *
   * @param userID ID пользователя
   * @param loginInfo Данные для входа
   * @return
   */
  def saveUserLoginInfo(userID: UUID, loginInfo: LoginInfo): Future[Unit]
}
