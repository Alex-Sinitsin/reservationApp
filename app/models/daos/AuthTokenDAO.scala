package models.daos

import java.time.ZonedDateTime
import java.util.UUID

import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider

import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

import models.{AuthToken, DBAuthToken}

/**
 * Предоставляет доступ к объекту `AuthToken`
 *
 * @param dbConfigProvider Провайдер конфигурации базы данных
 */
class AuthTokenDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider) extends DatabaseDAO {

  /**
   * Находит токен по его ID
   *
   * @param id Уникальный ID токена
   * @return Найденный токен или None, если токен с таким ID не был найден
   */
  def find(id: UUID): Future[Option[DBAuthToken]] = {
    db.run(authTokens.filter(_.id === id).result.headOption)
  }

  /**
   * Находит недействующие токены
   *
   */
  def findExpired(): Future[Seq[DBAuthToken]] = {
    db.run(authTokens.filter(_.expiry < AuthToken.toTimeStamp(ZonedDateTime.now())).result)
  }


  /**
   * Сохраняет токен
   *
   * @param token Данные токена
   * @return Сохраненный токен
   */
  def save(token: DBAuthToken): Future[Int] = {
    db.run(authTokens.insertOrUpdate(token))
  }

  /**
   * Удаляет токен с указанным ID
   *
   * @param id ID токена, который должен быть удален
   * @return Количество затронутых записей, возвращается в объекте Future
   */
  def remove(id: UUID): Future[Int] = {
    db.run(authTokens.filter(_.id === id).delete)
  }
}
