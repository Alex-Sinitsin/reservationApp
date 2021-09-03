package models.services

import models.DBAuthToken
import java.util.UUID

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Обрабатывает действия с токенами
 */
trait AuthTokenService {

  /**
   * Создает новый токен и сохраняет его в хранилище
   *
   * @param userID ID пользователя, для которого должен создаться новый токен
   * @param expiry Дата действия нового токена
   * @return Сохраненный токен авторизации
   */
  def create(userID: UUID, expiry: FiniteDuration = 5 minutes): Future[DBAuthToken]

  /**
   * Валидация токена по его ID
   *
   * @param id ID токена для проверки
   * @return Токен, если он действителен, иначе None
   */
  def validate(id: UUID): Future[Option[DBAuthToken]]

  /**
   * Очищает недействующие токены
   *
   * @return Список удаленных токенов
   */
  def clean: Future[Seq[DBAuthToken]]
}
