package models.services

import javax.inject.Inject

import models.daos.AuthTokenDAO
import models.{AuthToken, DBAuthToken}

import java.time.ZonedDateTime
import java.util.UUID

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Производит действия с токенами авторизации
 *
 * @param authTokenDAO Реализация объкта БД для работы с токенами
 * @param ex           Контекст выполнения
 */
class AuthTokenServiceImpl @Inject()(authTokenDAO: AuthTokenDAO)(implicit ex: ExecutionContext) extends AuthTokenService {

  /**
   * Создает новый токен и сохраняет его в хранилище
   *
   * @param userID ID пользователя, для которого должен создаться новый токен
   * @param expiry Дата действия нового токена
   * @return Сохраненный токен авторизации
   */
  def create(userID: UUID, expiry: FiniteDuration = 3 hours): Future[DBAuthToken] = {
    val token = DBAuthToken(UUID.randomUUID(), userID, AuthToken.toTimeStamp(ZonedDateTime.now().plusSeconds(expiry.toSeconds)))
    authTokenDAO.save(token).map(_ => token)
  }

  /**
   * Валидация токена по его ID
   *
   * @param id ID токена для проверки
   * @return Токен, если он действителен, иначе None
   */
  def validate(id: UUID): Future[Option[DBAuthToken]] = authTokenDAO.find(id)

  /**
   * Очищает недействующие токены
   *
   * @return Список удаленных токенов
   */
  def clean: Future[Seq[DBAuthToken]] = authTokenDAO.findExpired().flatMap { tokens =>
    Future.sequence(tokens.map { token =>
      authTokenDAO.remove(token.id).map(_ => token)
    })
  }
}
